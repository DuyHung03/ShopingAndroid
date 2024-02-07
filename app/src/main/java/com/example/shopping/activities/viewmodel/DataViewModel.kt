package com.example.shopping.activities.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping.activities.entities.CartItem
import com.example.shopping.activities.entities.User
import com.example.shopping.activities.repository.DataRepository
import com.example.shopping.activities.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _addToCartFlow = MutableStateFlow<Resources<String>?>(null)
    val addToCartFlow: StateFlow<Resources<String>?> = _addToCartFlow

    private val _productsCartFlow = MutableLiveData<Resources<List<CartItem>?>>()
    val productsCartFlow: LiveData<Resources<List<CartItem>?>> = _productsCartFlow

    private val _cartQuantity = MutableLiveData<Int>(0)
    val cartQuantity: LiveData<Int> = _cartQuantity

    fun saveUserToDb(newUser: User) = viewModelScope.launch {
        dataRepository.saveUserToDb(newUser)
    }

    fun checkIsUserExisted(userId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            dataRepository.checkIsUserExisted(userId) {
                callback(it)
            }
        }
    }

    fun addToCart(userId: String, cartItem: CartItem) = viewModelScope.launch {
        _addToCartFlow.value = Resources.Loading
        val result = dataRepository.addToCart(userId, cartItem)
        _addToCartFlow.value = result
    }

    fun getProductsInCart(userId: String) = viewModelScope.launch {
        _productsCartFlow.value = Resources.Loading
        val result = dataRepository.getProductsInCart(userId)
        when (result) {
            is Resources.Success -> {
                val cart = result.result
                if (cart != null) {
                    for (cartItem in cart) {
                        _cartQuantity.value = _cartQuantity.value?.plus(cartItem.quantity)
                    }
                }
            }

            else -> {}
        }
        _productsCartFlow.value = result
    }

}