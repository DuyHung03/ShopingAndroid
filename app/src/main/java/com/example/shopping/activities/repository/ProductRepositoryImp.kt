package com.example.shopping.activities.repository

import com.example.shopping.activities.entities.Category
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.network.ProductService
import com.example.shopping.activities.utils.Resources
import javax.inject.Inject

class ProductRepositoryImp @Inject constructor(
    private val productService: ProductService,
) : ProductRepository {

    override suspend fun getProductsAsPage(
        offset: Int,
        limit: Int,
    ): Resources<List<Product>> {
        return try {
            val result = productService.getProductsAsPage(offset, limit)
            if (result.isSuccessful) {
                val products = result.body()
                if (products != null) {
                    Resources.Success(products)
                } else
                    Resources.Failure(Exception("Empty product"))
            } else
                Resources.Failure(Exception(result.message()))
        } catch (e: Exception) {
            e.printStackTrace()
            Resources.Failure(e)
        }
    }

    override suspend fun getCategory(): Resources<List<Category>> {
        return try {
            val result = productService.getCategories()
            if (result.isSuccessful) {
                val category = result.body()
                if (category != null) {
                    Resources.Success(category)
                } else
                    Resources.Failure(Exception("Empty"))
            } else
                Resources.Failure(Exception(result.message()))
        } catch (e: Exception) {
            e.printStackTrace()
            Resources.Failure(e)
        }
    }

}