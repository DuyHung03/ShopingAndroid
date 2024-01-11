package com.example.shopping.activities.view.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.example.shopping.R
import com.example.shopping.activities.helper.InputValidationHelper
import com.example.shopping.activities.helper.KeyboardUtils
import com.example.shopping.activities.helper.ValidationTextWatcher
import com.example.shopping.activities.utils.Resources
import com.example.shopping.activities.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
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

    @SuppressLint("LogNotTimber")
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("TAG", "Got ID token.")
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        Toast.makeText(this, credential.id, Toast.LENGTH_LONG)
                            .show()
                        Log.d(
                            "TAG",
                            "onCreateView: $idToken ${credential.password} ${credential.displayName}${credential.profilePictureUri}"
                        )

                    } else {
                        Log.d("TAG", "No ID token!")
                    }
                } catch (e: ApiException) {
                    Log.d("TAG", e.toString())
                }
            }
        }

    private val viewModel by viewModels<AuthViewModel>()

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
        initializeGoogleSignIn()
        setupListeners()

        viewModel.stateFlow.asLiveData().observe(this) { state ->
            when (state) {
                is Resources.Success -> {
                    val user = state.result
                    Log.d("TAG", "onCreateView: $user")
                    viewModel.currentUser = user
                }

                is Resources.Failure -> {
                    val error = state.e
                    Log.d("TAG", "onCreateView: $error")
                    progressBar.visibility = View.GONE
                }

                Resources.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                else -> {}
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


    @SuppressLint("LogNotTimber")
    private fun setupListeners() {
        val validationTextWatcher = ValidationTextWatcher(listOf(email, password))
        email.editText?.addTextChangedListener(validationTextWatcher)
        password.editText?.addTextChangedListener(validationTextWatcher)

        keyboardUtils.clearFocus(listOf(email.editText, password.editText))

        loginButton.setOnClickListener {
            val emailText = email.editText?.text.toString().trim()
            val passwordText = password.editText?.text.toString().trim()

            Log.d("TAG", "handleLoginEmail: $emailText")
            handleLoginEmail(emailText, passwordText)
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
            initiateGoogleSignIn()
        }

    }

    private fun handleLoginEmail(email: String, password: String) {
        viewModel.loginWithEmail(email, password)
    }

    private fun initiateGoogleSignIn() {
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

        return (inputValidator.isValidEmail(emailText) { error ->
            email.error = error
        } && inputValidator.isValidPassword(passwordText) { error ->
            password.error = error
        })
    }

}