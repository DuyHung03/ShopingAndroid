package com.example.shopping.activities.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping.activities.repository.AuthRepository
import com.example.shopping.activities.utils.Resources
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    var currentUser: FirebaseUser? = null
        get() = authRepository.currentUser

    init {
        if (authRepository.currentUser != null) {
            Resources.Success(authRepository.currentUser)
        }
    }

    private val _loginFlow = MutableStateFlow<Resources<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resources<FirebaseUser>?> = _loginFlow

    private val _registerFlow = MutableStateFlow<Resources<FirebaseUser>?>(null)
    val registerFlow: StateFlow<Resources<FirebaseUser>?> = _registerFlow

    fun loginWithEmail(email: String, password: String) = viewModelScope.launch {
        _loginFlow.value = Resources.Loading
        val result = authRepository.loginWithEmail(email, password)
        _loginFlow.value = result
    }

    fun signUp(name: String, email: String, password: String) = viewModelScope.launch {
        _registerFlow.value = Resources.Loading
        val result = authRepository.signUp(name, email, password)
        _registerFlow.value = result
    }

    fun logout() {
        authRepository.logout()
        _loginFlow.value = null
    }

}