package com.example.shopping.activities.repository

import com.example.shopping.activities.entities.CartItem
import com.example.shopping.activities.entities.User
import com.example.shopping.activities.utils.Resources

interface DataRepository {
    suspend fun saveUserToDb(newUser: User)
    suspend fun checkIsUserExisted(userId: String, callback: (Boolean) -> Unit)

    suspend fun addToCart(userId: String, cartItem: CartItem): Resources<String>
    suspend fun getProductsInCart(userId: String): Resources<List<CartItem>?>
}