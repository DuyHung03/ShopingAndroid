package com.example.shopping.activities.viewmodel

import android.icu.text.CaseMap.Title
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shopping.activities.entities.Category
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.repository.ProductRepository
import com.example.shopping.activities.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val _categories = MutableLiveData<Resources<List<Category>>>()
    val categories: LiveData<Resources<List<Category>>> get() = _categories

    private val _products = MutableLiveData<Resources<List<Product>>>()
    val products: LiveData<Resources<List<Product>>> get() = _products


    fun getProductsAsPage(): Flow<PagingData<Product>> {
        return productRepository.getProductsAsPage().cachedIn(viewModelScope)
    }

    fun getCategory() = viewModelScope.launch {
        _categories.value = Resources.Loading
        val result = productRepository.getCategory()
        _categories.value = result
    }

    fun getProductsByTitle(title: String) = viewModelScope.launch {
        _products.value = Resources.Loading
        val result = productRepository.getProductByTitle(title)
        _products.value = result
    }
}