package com.example.shopping.activities.di

import com.example.shopping.activities.network.CityService
import com.example.shopping.activities.network.ProductService
import com.example.shopping.activities.repository.CityRepository
import com.example.shopping.activities.repository.CityRepositoryImp
import com.example.shopping.activities.repository.ProductRepository
import com.example.shopping.activities.repository.ProductRepositoryImp
import com.example.shopping.activities.utils.Constant.Companion.CITY_BASE_URL
import com.example.shopping.activities.utils.Constant.Companion.PRODUCT_BASE_URL
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
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Named("productRetrofit")
    fun provideProductRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(PRODUCT_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .build()

    @Provides
    @Named("cityRetrofit")
    fun provideCityRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(CITY_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .build()

    @Provides
    fun provideProductService( @Named("productRetrofit") retrofit: Retrofit): ProductService =
        retrofit.create(ProductService::class.java)

    @Provides
    fun provideCityService( @Named("cityRetrofit") retrofit: Retrofit): CityService =
        retrofit.create(CityService::class.java)

    @Provides
    fun provideProductRepository(productRepositoryImp: ProductRepositoryImp): ProductRepository =
        productRepositoryImp
    @Provides
    fun provideCityRepository(cityRepositoryImp: CityRepositoryImp): CityRepository =
        cityRepositoryImp
}