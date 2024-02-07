package com.example.shopping.activities.di

import com.example.shopping.activities.repository.AuthRepository
import com.example.shopping.activities.repository.AuthRepositoryImp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseUser(): FirebaseUser? = Firebase.auth.currentUser

    @Provides
    fun provideAuthRepository(impl: AuthRepositoryImp): AuthRepository = impl
}