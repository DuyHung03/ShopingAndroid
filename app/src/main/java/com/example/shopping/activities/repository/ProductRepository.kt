package com.example.shopping.activities.repository

import com.example.shopping.activities.entities.Category
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.utils.Resources
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProductsAsPage(offset: Int, limit: Int): Resources<List<Product>>
    suspend fun getCategory(): Resources<List<Category>>
    suspend fun getProductByTitle(title: String): Resources<List<Product>>
    suspend fun getProductsByCategory(id: Int): Resources<List<Product>>
}