package com.example.shopping.activities.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.network.ProductService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ProductPagingSource @Inject constructor(
    private val productService: ProductService,
) :
    PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val offset = params.key ?: 0
            val limit = 10
            val response = productService.getProductsAsPage(offset, limit)

            if (response.isSuccessful) {
                val products = response.body() ?: emptyList()
                LoadResult.Page(
                    data = products,
                    prevKey = if (offset == 0) null else offset - limit,
                    nextKey = if (products.isEmpty()) null else offset + limit
                )
            } else {
                LoadResult.Error(IOException("Failed to fetch data"))
            }
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        // This is used fo  r subsequent calls to PagingSource.load after the initial load
        // Returning null means no further pagination is possible.
        return state.anchorPosition?.let { state.closestItemToPosition(it)?.id }
    }
}
