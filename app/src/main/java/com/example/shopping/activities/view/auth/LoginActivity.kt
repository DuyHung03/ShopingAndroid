package com.example.shopping.activities.view.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.example.shopping.R
import com.example.shopping.activities.entities.User
import com.example.shopping.activities.helper.InputValidationHelper
import com.example.shopping.activities.helper.KeyboardUtils
import com.example.shopping.activities.helper.ValidationTextWatcher
import com.example.shopping.activities.utils.Resources
import com.example.shopping.activities.view.activities.MainActivity
import com.example.shopping.activities.viewmodel.AuthViewModel
import com.example.shopping.activities.viewmodel.DataViewModel
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("LogNotTimber")
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: CircularProgressButton
    private lateinit var googleButton: SignInButton
    private lateinit var email: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var closeButton: ImageView
    private lateinit var toRegisterButton: TextView
    private val inputValidator = InputValidationHelper()
    private val keyboardUtils = KeyboardUtils()
    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private lateinit var resultLauncher: ActivityResultLauncher<IntentSenderRequest>


    private val viewModel by viewModels<AuthViewModel>()
    private val dataViewModel by viewModels<DataViewModel>()

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
        initializeGoogleSignIn()
        initialListeners()

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d("TAG", "Got ID token.")
                    try {
                        val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                        val idToken = credential.googleIdToken
                        if (idToken != null) {
                            val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                            viewModel.loginWithGoogle(authCredential)
                        } else {
                            Log.d("TAG", "No ID token!")
                        }
                    } catch (e: ApiException) {
                        Log.d("TAG", e.toString())
                    }
                }
            }

    }

    private fun initializeViews() {
        loginButton = findViewById(R.id.loginButton)
        email = findViewById(R.id.emailInputLayout)
        password = findViewById(R.id.passwordInputLayout)
        progressBar = findViewById(R.id.progressBar)
        closeButton = findViewById(R.id.closeButton)
        toRegisterButton = findViewById(R.id.toRegisterButton)

        googleButton = findViewById(R.id.google_button)
        googleButton.setSize(SignInButton.SIZE_WIDE)
        googleButton.setColorScheme(SignInButton.COLOR_LIGHT)
    }

    private fun initializeGoogleSignIn() {
        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = buildSignInRequest()
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }


    private fun initialListeners() {
        //input layout watcher
        val validationTextWatcher = ValidationTextWatcher(listOf(email, password))
        email.editText?.addTextChangedListener(validationTextWatcher)
        password.editText?.addTextChangedListener(validationTextWatcher)

        //hide keyboard when un focus
        keyboardUtils.clearFocus(listOf(email.editText, password.editText))

        loginButton.setOnClickListener {
            val emailText = email.editText?.text.toString().trim()
            val passwordText = password.editText?.text.toString().trim()

            if (inputValidation())
                viewModel.loginWithEmail(emailText, passwordText)
        }

        //observe the login flow to get user
        viewModel.loginFlow.asLiveData().observe(this) { state ->
            when (state) {
                is Resources.Success -> {
                    val user = state.result

                    //check is user exists then write to database
                    if (user.providerData[1].providerId == "google.com") {
                        dataViewModel.checkIsUserExisted(user.uid) {
                            if (it) {
                                val newUser = User(
                                    user.uid,
                                    user.email,
                                    user.displayName,
                                    password = null,
                                    user.photoUrl.toString(),
                                    user.providerData[1].providerId
                                )
                                dataViewModel.saveUserToDb(newUser)
                            }
                        }
                    }

                    viewModel.currentUser = user
                    redirectHomeScreen()
                }

                is Resources.Failure -> {
                    val error = state.e
                    Log.d("TAG", "onCreateView: $error")
                    Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                    loginButton.revertAnimation()
                }

                is Resources.Loading -> {
                    loginButton.startAnimation()
                }

                else -> {}
            }
        }

        closeButton.setOnClickListener {
            this.finish()
        }

        toRegisterButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(
                intent,
                ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
                    .toBundle()
            )
        }

        googleButton.setOnClickListener {
            googleSignIn()
        }

    }

    private fun redirectHomeScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(
            intent,
            ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
                .toBundle()
        )
        this.finish()
    }

    private fun googleSignIn() {
        oneTapClient.beginSignIn(signUpRequest).addOnSuccessListener { result ->
            val intentSenderRequest =
                IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
            resultLauncher.launch(intentSenderRequest)
        }.addOnFailureListener { e ->
            // No Google Accounts found. Just continue presenting the signed-out UI.
            Log.d("TAG", e.localizedMessage!!)
        }
    }

    private fun inputValidation(): Boolean {
        val emailText = email.editText?.text.toString().trim()
        val passwordText = password.editText?.text.toString().trim()

        Log.d("TAG", "handleLoginEmail: $emailText")
        return (inputValidator.isValidEmail(emailText) { error ->
            email.error = error
        } && inputValidator.isValidPassword(passwordText) { error ->
            password.error = error
        })
    }

}