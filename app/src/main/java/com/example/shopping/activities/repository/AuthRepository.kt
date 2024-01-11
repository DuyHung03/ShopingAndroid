package com.example.shopping.activities.repository

import com.example.shopping.activities.utils.Resources
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun googleLogin(): Resources<FirebaseUser>
    suspend fun loginWithEmail(email: String, password: String): Resources<FirebaseUser>
    suspend fun signUp(name: String, email: String, password: String): Resources<FirebaseUser>
    fun logout()
}