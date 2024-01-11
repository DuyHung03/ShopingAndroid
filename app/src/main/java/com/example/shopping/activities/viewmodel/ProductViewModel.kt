package com.example.shopping.activities.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _product = MutableLiveData<Resources<List<Product>>>()
    val product: LiveData<Resources<List<Product>>> get() = _product

    private val _categories = MutableLiveData<Resources<List<Category>>>()
    val categories: LiveData<Resources<List<Category>>> get() = _categories

    fun getProductAsPage(offset: Int, limit: Int) = viewModelScope.launch {
        _product.value = Resources.Loading
        val result = productRepository.getProductsAsPage(offset, limit)
        _product.value = result
    }

    fun getCategory() = viewModelScope.launch {
        _categories.value = Resources.Loading
        val result = productRepository.getCategory()
        _categories.value = result
    }

}