package com.example.shopping.activities.repository

import android.util.Log
import com.example.shopping.activities.utils.Resources
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {
    override var currentUser: FirebaseUser? = null
        get() = firebaseAuth.currentUser

    override suspend fun googleLogin(token: AuthCredential): Resources<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithCredential(token).await()
            Resources.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resources.Failure(e)
        }
    }

    override suspend fun loginWithEmail(email: String, password: String): Resources<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resources.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resources.Failure(e)
        }
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
    ): Resources<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user!!.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            ).await()
            Log.d("TAG", "signUp: set name")
            Resources.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Resources.Failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}