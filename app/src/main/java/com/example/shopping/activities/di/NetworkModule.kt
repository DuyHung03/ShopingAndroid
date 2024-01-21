package com.example.shopping.activities.di

import com.example.shopping.activities.network.ProductService
import com.example.shopping.activities.repository.ProductRepository
import com.example.shopping.activities.repository.ProductRepositoryImp
import com.example.shopping.activities.utils.Constant.Companion.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .build()

    @Provides
    fun provideProductService(retrofit: Retrofit): ProductService =
        retrofit.create(ProductService::class.java)

    @Provides
    fun provideProductRepository(productRepositoryImp: ProductRepositoryImp): ProductRepository =
        productRepositoryImp
}