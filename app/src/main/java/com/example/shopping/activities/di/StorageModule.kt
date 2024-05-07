package com.example.shopping.activities.di

import com.example.shopping.activities.repository.DataRepository
import com.example.shopping.activities.repository.DataRepositoryImp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class StorageModule {
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideDataRepository(impl: DataRepositoryImp): DataRepository = impl

    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}