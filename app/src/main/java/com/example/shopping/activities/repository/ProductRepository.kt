package com.example.shopping.activities.repository

import androidx.paging.PagingData
import com.example.shopping.activities.entities.Category
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.utils.Resources
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProductsAsPage(): Flow<PagingData<Product>>
    suspend fun getCategory(): Resources<List<Category>>
    suspend fun getProductByTitle(title: String): Resources<List<Product>>
    suspend fun getProductsByCategory(id: Int): Resources<List<Product>>
}