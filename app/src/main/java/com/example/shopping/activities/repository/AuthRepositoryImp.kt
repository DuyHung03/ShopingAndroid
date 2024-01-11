package com.example.shopping.activities.repository

import com.example.shopping.activities.utils.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {
    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun googleLogin(): Resources<FirebaseUser> {
        TODO("Not yet implemented")
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
            )
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