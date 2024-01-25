package com.example.shopping.activities.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shopping.activities.entities.Category
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.network.ProductService
import com.example.shopping.activities.paging.ProductPagingSource
import com.example.shopping.activities.utils.Resources
import kotlinx.coroutines.flow.Flow
import java.net.URLEncoder
import javax.inject.Inject

class ProductRepositoryImp @Inject constructor(
    private val productService: ProductService,
) : ProductRepository {

    override fun getProductsAsPage(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10, // Number of items to load in each page
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ProductPagingSource(productService) }
        ).flow
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

    override suspend fun getProductByTitle(title: String): Resources<List<Product>> {
        return try {
            val query = URLEncoder.encode(title, "UTF-8")
            val result = productService.getProductsByTitle(query)
            if (result.isSuccessful) {
                val products = result.body()
                if (products != null) {
                    Resources.Success(products)
                } else {
                    Resources.Failure(Exception("Empty"))
                }
            } else
                Resources.Failure(Exception(result.message()))
        } catch (e: Exception) {
            e.printStackTrace()
            Resources.Failure(e)
        }
    }

    override suspend fun getProductsByCategory(id: Int): Resources<List<Product>> {
        return try {
            val result = productService.getProductsByCategory(id)
            if (result.isSuccessful) {
                val products = result.body()
                if (products != null) {
                    Resources.Success(products)
                } else {
                    Resources.Failure(Exception("Empty"))
                }
            } else {
                Resources.Failure(Exception(result.message()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resources.Failure(e)
        }
    }
}