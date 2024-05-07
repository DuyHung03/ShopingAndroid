package com.example.shopping.activities.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.shopping.activities.entities.Address
import com.example.shopping.activities.entities.CancelOrder
import com.example.shopping.activities.entities.CartItem
import com.example.shopping.activities.entities.Message
import com.example.shopping.activities.entities.Order
import com.example.shopping.activities.entities.OrderList
import com.example.shopping.activities.entities.User
import com.example.shopping.activities.utils.Resources

interface DataRepository {
    suspend fun saveUserToDb(newUser: User)
    suspend fun checkIsUserExisted(userId: String, callback: (Boolean) -> Unit)

    suspend fun addToCart(userId: String, cartItem: CartItem): Resources<String>
    suspend fun getProductsInCart(userId: String): Resources<List<CartItem>?>

    suspend fun increaseQuantity(cartItem: CartItem): Resources<String>
    suspend fun decreaseQuantity(cartItem: CartItem): Resources<String>

    suspend fun deleteProductInCart(cartItem: CartItem): Resources<List<CartItem>>

    suspend fun getDeliveryAddress(userId: String): Resources<List<Address>>

    suspend fun saveAddress(address: Address, userId: String): Resources<String>

    suspend fun saveOrder(order: Order, userId: String): Resources<String>
    suspend fun getOrders(userId: String): Resources<OrderList>

    suspend fun cancelOrder(order: Order, userId: String, reason: String): Resources<String>

    suspend fun deleteOrderInDatabase(cancelOrder: CancelOrder, userId: String)

    suspend fun sendMessage(message: Message, conversationId: String)

    fun getMessage(userId: String, liveData: MutableLiveData<List<Message>>)

    suspend fun saveMessageToLocal(message: Message)
    suspend fun getMessageFromLocal(senderId: String): List<Message>

    suspend fun uploadImage(uri: Uri): Uri

}