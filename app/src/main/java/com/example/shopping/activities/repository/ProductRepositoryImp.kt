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

}