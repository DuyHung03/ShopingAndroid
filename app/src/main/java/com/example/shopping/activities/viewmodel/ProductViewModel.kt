package com.example.shopping.activities.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping.activities.entities.CartItem
import com.example.shopping.activities.entities.Category
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.repository.ProductRepository
import com.example.shopping.activities.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
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


    fun getProductAsPage(offset: Int, limit: Int) = viewModelScope.launch {
        _products.value = Resources.Loading
        val result = productRepository.getProductsAsPage(offset, limit)
        _products.value = result
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

    fun getProductByCategory(id: Int) = viewModelScope.launch {
        _products.value = Resources.Loading
        val result = productRepository.getProductsByCategory(id)
        _products.value = result
    }

    fun handleCheckedProducts(products: List<CartItem>) = viewModelScope.launch {

    }

}