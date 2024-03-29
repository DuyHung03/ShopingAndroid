package com.example.shopping.activities.view.auth

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.example.shopping.R
import com.example.shopping.activities.entities.User
import com.example.shopping.activities.helper.InputValidationHelper
import com.example.shopping.activities.helper.ValidationTextWatcher
import com.example.shopping.activities.utils.Resources
import com.example.shopping.activities.view.activities.MainActivity
import com.example.shopping.activities.viewmodel.AuthViewModel
import com.example.shopping.activities.viewmodel.DataViewModel
import com.example.shopping.databinding.ActivityRegisterBinding
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("LogNotTimber")
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var backButton: ImageView
    private lateinit var email: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var displayName: TextInputLayout
    private lateinit var confirmPassword: TextInputLayout
    private lateinit var registerButton: CircularProgressButton
    private val inputValidator = InputValidationHelper()
    private val viewModel by viewModels<AuthViewModel>()
    private val dataViewModel by viewModels<DataViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialView()
        initialListener()
    }

    private fun initialView() {
        backButton = binding.backButton
        email = binding.emailInputLayout
        password = binding.passwordInputLayout
        displayName = binding.displayNameInputLayout
        confirmPassword = binding.confirmPasswordInputLayout
        registerButton = binding.registerButton
    }

    private fun initialListener() {
        backButton.setOnClickListener { this.finish() }

        val validationTextWatcher =
            ValidationTextWatcher(listOf(email, password, confirmPassword, displayName))
        email.editText?.addTextChangedListener(validationTextWatcher)
        displayName.editText?.addTextChangedListener(validationTextWatcher)
        password.editText?.addTextChangedListener(validationTextWatcher)
        confirmPassword.editText?.addTextChangedListener(validationTextWatcher)


        registerButton.setOnClickListener {
            val emailText = email.editText?.text.toString().trim()
            val passwordText = password.editText?.text.toString().trim()
            val displayNameText = displayName.editText?.text.toString().trim()

            if (inputValidation())
                viewModel.signUp(displayNameText, emailText, passwordText)
        }

        viewModel.registerFlow.asLiveData().observe(this) { state ->
            when (state) {
                is Resources.Success -> {
                    val user = state.result
                    viewModel.currentUser = user
                    val newUser = User(
                        user.uid,
                        user.email,
                        user.displayName,
                        password.editText!!.text.toString(),
                        user.photoUrl.toString(),
                        user.providerData[1].providerId
                    )
                    dataViewModel.saveUserToDb(newUser)
                    redirectHomeScreen()
                }

                is Resources.Failure -> {
                    val error = state.e
                    Log.d("TAG", "onCreateView: $error")
                    Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                    registerButton.revertAnimation()
                }

                is Resources.Loading -> {
                    registerButton.startAnimation()
                }

                else -> {}
            }
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

    private fun inputValidation(): Boolean {
        val emailText = email.editText?.text.toString().trim()
        val passwordText = password.editText?.text.toString().trim()
        val confirmPasswordText = confirmPassword.editText?.text.toString().trim()
        val displayNameText = displayName.editText?.text.toString().trim()

        Log.d("TAG", "handleLoginEmail: $emailText")
        return (
                inputValidator.isValidEmail(emailText) { error ->
                    email.error = error
                } && inputValidator.isValidPassword(passwordText) { error ->
                    password.error = error
                } && inputValidator.isValidPassword(displayNameText) { error ->
                    displayName.error = error
                } && inputValidator.isValidPassword(confirmPasswordText) { error ->
                    confirmPassword.error = error
                } && inputValidator.isMatchPassword(passwordText, confirmPasswordText) { error ->
                    confirmPassword.error = error
                }
                )
    }

}