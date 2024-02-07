package com.example.shopping.activities.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.shopping.activities.entities.Cart
import com.example.shopping.activities.entities.CartItem
import com.example.shopping.activities.entities.User
import com.example.shopping.activities.utils.Resources
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@SuppressLint("LogNotTimber")
class DataRepositoryImp @Inject constructor(
    private val db: FirebaseFirestore,
) : DataRepository {
    private val cartDocument = db.collection("cart")

    override suspend fun saveUserToDb(newUser: User) {
        try {
            db.collection("user")
                .document(newUser.userId)
                .set(newUser)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("TAG", "saveUserToDb: Add ${newUser.email} to db successfully")
                    } else {
                        Log.d("TAG", "Error")
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun checkIsUserExisted(userId: String, callback: (Boolean) -> Unit) {
        try {
            db.collection("user")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    callback(document != null)
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                    callback(false)
                }
        } catch (e: Exception) {
            e.printStackTrace()
            callback(false)
        }
    }

    override suspend fun addToCart(userId: String, cartItem: CartItem): Resources<String> {
        val cartDocumentRef = cartDocument.document(userId)

        try {
            // Get the current user's cart document
            val cartSnapshot = cartDocumentRef.get().await()

            if (cartSnapshot.exists()) {
                // Document exists, update the cart items map
                val existingCart = cartSnapshot.toObject<Cart>()
                existingCart?.let { cart ->
                    val updatedCartItems = cart.cartItem.toMutableMap()

                    if (updatedCartItems.containsKey(cartItem.productId)) {
                        // Product already exists in cart, update the quantity
                        val existingCartItem = updatedCartItems[cartItem.productId]
                        existingCartItem?.let {
                            existingCartItem.quantity += cartItem.quantity
                            updatedCartItems[cartItem.productId] = existingCartItem
                        }
                    } else {
                        // Product doesn't exist in cart, add a new cart item
                        updatedCartItems[cartItem.productId] = cartItem
                    }

                    // Update the cart document with the updated cart items
                    cartDocumentRef.set(cart.copy(cartItem = updatedCartItems)).await()
                }
            } else {
                // Document doesn't exist, create a new document with the cart item
                cartDocumentRef.set(Cart(cartItem = mapOf(cartItem.productId to cartItem))).await()
            }

            return Resources.Success("Added to cart successfully")
        } catch (e: FirebaseFirestoreException) {
            return Resources.Failure(Exception(e.message))
        } catch (e: Exception) {
            return Resources.Failure(Exception(e.message))
        }
    }


    override suspend fun getProductsInCart(userId: String): Resources<List<CartItem>> {
        val cartDocumentRef = cartDocument.document(userId)

        return try {
            // Get the current user's cart document
            val cartSnapshot = cartDocumentRef.get().await()

            if (cartSnapshot.exists()) {
                // Document exists, retrieve cart items
                val cart = cartSnapshot.toObject<Cart>()
                cart?.let { cartData ->
                    val cartItems = cartData.cartItem.values.toList()
                    Resources.Success(cartItems)
                } ?: Resources.Failure(Exception("Failed to retrieve cart items"))
            } else {
                // Document doesn't exist, cart is empty
                Resources.Success(emptyList())
            }
        } catch (e: FirebaseFirestoreException) {
            Resources.Failure(Exception(e.message))
        } catch (e: Exception) {
            Resources.Failure(Exception(e.message))
        }
    }

}