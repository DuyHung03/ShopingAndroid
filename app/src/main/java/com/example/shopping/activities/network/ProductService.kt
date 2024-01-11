package com.example.shopping.activities.network

import com.example.shopping.activities.entities.Category
import com.example.shopping.activities.entities.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductService {

    @GET("products")
    suspend fun getProductsAsPage(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Response<List<Product>>

    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

}