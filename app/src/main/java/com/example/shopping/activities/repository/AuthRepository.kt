package com.example.shopping.activities.repository

import com.example.shopping.activities.utils.Resources
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    var currentUser: FirebaseUser?
    suspend fun googleLogin(token: AuthCredential): Resources<FirebaseUser>
    suspend fun loginWithEmail(email: String, password: String): Resources<FirebaseUser>
    suspend fun signUp(name: String, email: String, password: String): Resources<FirebaseUser>
    fun logout()
}