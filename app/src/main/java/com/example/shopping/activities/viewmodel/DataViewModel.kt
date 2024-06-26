package com.example.shopping.activities.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopping.activities.entities.Address
import com.example.shopping.activities.entities.CartItem
import com.example.shopping.activities.entities.Message
import com.example.shopping.activities.entities.Order
import com.example.shopping.activities.entities.User
import com.example.shopping.activities.repository.DataRepository
import com.example.shopping.activities.utils.Resources
import com.google.firebase.auth.FirebaseUser
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


    private val _productsCartFlow = MutableLiveData<List<CartItem>?>()
    val productsCartFlow: LiveData<List<CartItem>?> = _productsCartFlow

    private val _addressFlow = MutableLiveData<Resources<List<Address>>>()
    val addressFlow: LiveData<Resources<List<Address>>> = _addressFlow

    private val _cartQuantity = MutableLiveData(0)
    val cartQuantity: LiveData<Int> = _cartQuantity

    private val _saveAddressFlow = MutableLiveData<Resources<String>>()
    val saveAddressFlow: LiveData<Resources<String>> = _saveAddressFlow

    private val _saveOrderFlow = MutableLiveData<Resources<String>>()
    val saveOrderFlow: LiveData<Resources<String>> = _saveOrderFlow

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


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
        val result = dataRepository.addToCart(userId, cartItem)
        _addToCartFlow.value = result
    }

    fun getProductsInCart(userId: String) = viewModelScope.launch {
        when (val result = dataRepository.getProductsInCart(userId)) {
            is Resources.Success -> {
                val cart = result.result
                if (cart != null) {
                    _cartQuantity.value = cart.size
                }
                _productsCartFlow.value = cart
            }

            is Resources.Failure -> _errorMessage.value = result.e.message
            else -> {}
        }
    }

    fun increaseQuantity(cartItem: CartItem) = viewModelScope.launch {
        dataRepository.increaseQuantity(cartItem)
    }

    fun deleteProductInCart(cartItem: CartItem) = viewModelScope.launch {
        when (val result = dataRepository.deleteProductInCart(cartItem)) {
            is Resources.Success -> {
                val updatedList = result.result
                _productsCartFlow.value = updatedList
                _cartQuantity.value = updatedList.size
            }

            is Resources.Failure -> _errorMessage.value = result.e.message

            else -> {}
        }
    }

    fun getDeliveryAddress(userId: String) = viewModelScope.launch {
        _addressFlow.value = Resources.Loading
        val result = dataRepository.getDeliveryAddress(userId)
        _addressFlow.value = result
    }

    fun saveAddress(address: Address, userId: String) = viewModelScope.launch {
        _saveAddressFlow.value = Resources.Loading
        val res = dataRepository.saveAddress(address, userId)
        _saveAddressFlow.value = res
    }

    fun saveOrder(order: Order, userId: String) = viewModelScope.launch {
        _saveOrderFlow.value = Resources.Loading
        val res = dataRepository.saveOrder(order, userId)
        _saveOrderFlow.value = res
    }

//    private val _getOrdersFlow = MutableLiveData<Resources<OrderList>>()
//    val getOrdersFlow: LiveData<Resources<OrderList>> = _getOrdersFlow


    // Define waitingList and confirmedList
    private val _waitingList = MutableLiveData<List<Order>>()
    val waitingList: LiveData<List<Order>> = _waitingList

    private val _confirmedList = MutableLiveData<List<Order>>()
    val confirmedList: LiveData<List<Order>> = _confirmedList

    private val _cancelledList = MutableLiveData<List<Order>>()
    val cancelledList: LiveData<List<Order>> = _cancelledList

    fun getOrders(userId: String) = viewModelScope.launch {
        when (val res = dataRepository.getOrders(userId)) {
            is Resources.Success -> {
                val waitingList = mutableListOf<Order>()
                val confirmedList = mutableListOf<Order>()
                val cancelledList = mutableListOf<Order>()
                for (order in res.result.orderList.values) {
                    if (order.confirmed && !order.cancelled) {
                        confirmedList.add(order)
                    }
                    if (!order.confirmed && !order.cancelled) {
                        waitingList.add(order)
                    }
                    if (order.cancelled) {
                        cancelledList.add(order)
                    }
                }
                // Update LiveData lists
                _waitingList.value = waitingList
                _confirmedList.value = confirmedList
                _cancelledList.value = cancelledList
            }

            else -> {} // Handle other cases if needed
        }
    }

    private val _cancelOrderFlow = MutableLiveData<Resources<String>>()
    val cancelOrderFlow: LiveData<Resources<String>> = _cancelOrderFlow

    fun cancelOrder(order: Order, userId: String, reason: String) = viewModelScope.launch {
        _cancelOrderFlow.value = Resources.Loading
        val res = dataRepository.cancelOrder(order, userId, reason)
        _cancelOrderFlow.value = res
    }
    fun sentMessage(message: Message, conversationId: String) = viewModelScope.launch {
        try {
            dataRepository.sendMessage(message, conversationId)
        } catch (e: Exception) {
            Log.e("TAG", "Error sending message and refreshing messages: ${e.message}")
        }
    }

    private val _messagesLiveData = MutableLiveData<List<Message>>()
    val messagesLiveData: LiveData<List<Message>> = _messagesLiveData
    fun getMessageByUser(userId: String) {
        try {
            dataRepository.getMessage(userId, _messagesLiveData)
        } catch (e: Exception) {
            Log.d("TAG", "getChatUserList: $e")
        }
    }

    fun getMessageFromLocal(senderId: String) = viewModelScope.launch {
        val res = dataRepository.getMessageFromLocal(senderId)
        _messagesLiveData.value = res
    }

}