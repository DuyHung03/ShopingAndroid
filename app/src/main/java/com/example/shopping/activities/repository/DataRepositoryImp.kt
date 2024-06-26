package com.example.shopping.activities.repository

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.shopping.activities.entities.Address
import com.example.shopping.activities.entities.CancelOrder
import com.example.shopping.activities.entities.Cart
import com.example.shopping.activities.entities.CartItem
import com.example.shopping.activities.entities.Message
import com.example.shopping.activities.entities.Order
import com.example.shopping.activities.entities.OrderList
import com.example.shopping.activities.entities.User
import com.example.shopping.activities.roomDB.AppDatabase
import com.example.shopping.activities.utils.Resources
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@SuppressLint("LogNotTimber")
class DataRepositoryImp @Inject constructor(
    private val db: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val realtimeDatabase: FirebaseDatabase,
    private val appDatabase: AppDatabase,
    private val storage: FirebaseStorage
) : DataRepository {
    private val cartDocument = db.collection("cart")

    override suspend fun saveUserToDb(newUser: User) {
        try {
            db.collection("user").document(newUser.userId).set(newUser).addOnCompleteListener {
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
            db.collection("user").document(userId).get().addOnSuccessListener { document ->
                callback(document != null)
            }.addOnFailureListener { exception ->
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

    override suspend fun increaseQuantity(cartItem: CartItem): Resources<String> {
        return try {
            val cartDocumentRef = authRepository.currentUser?.let { cartDocument.document(it.uid) }
            val cartSnapshot = cartDocumentRef!!.get().await()

            if (cartSnapshot.exists()) {
                val cartData = cartSnapshot.toObject(Cart::class.java)
                val existingItem = cartData?.cartItem?.get(cartItem.productId)

                if (existingItem != null) {
                    val updatedQuantity = existingItem.quantity + 1
                    cartDocumentRef.update(
                        "cartItem.${existingItem.productId}.quantity", updatedQuantity
                    ).await()
                    Resources.Success("Quantity increased successfully")
                } else {
                    Resources.Failure(Exception("Item not found in the cart"))
                }
            } else {
                Resources.Failure(Exception("Cart document does not exist"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resources.Failure(e)
        }
    }

    override suspend fun decreaseQuantity(cartItem: CartItem): Resources<String> {
        return try {
            val cartDocumentRef = authRepository.currentUser?.let { cartDocument.document(it.uid) }
            val cartSnapshot = cartDocumentRef!!.get().await()

            if (cartSnapshot.exists()) {
                val cartData = cartSnapshot.toObject(Cart::class.java)
                val existingItem = cartData?.cartItem?.get(cartItem.productId)

                if (existingItem != null) {
                    val updatedQuantity = existingItem.quantity - 1
                    cartDocumentRef.update(
                        "cartItem.${existingItem.productId}.quantity", updatedQuantity
                    ).await()
                    Resources.Success("Quantity increased successfully")
                } else {
                    Resources.Failure(Exception("Item not found in the cart"))
                }
            } else {
                Resources.Failure(Exception("Cart document does not exist"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resources.Failure(e)
        }
    }

    override suspend fun deleteProductInCart(cartItem: CartItem): Resources<List<CartItem>> {
        return try {
            val currentUser = authRepository.currentUser
            if (currentUser != null) {
                val cartDocumentRef = cartDocument.document(currentUser.uid)
                val cartSnapshot = cartDocumentRef.get().await()

                if (cartSnapshot.exists()) {
                    val cartData = cartSnapshot.toObject(Cart::class.java)
                    val existingItem = cartData?.cartItem?.get(cartItem.productId)

                    // Check if the product exists in the cart
                    if (existingItem != null) {
                        val updates = hashMapOf<String, Any>(
                            "cartItem.${cartItem.productId}" to FieldValue.delete()
                        )

                        // Update the cart data in db
                        cartDocumentRef.update(updates).await()

                        // Return the updated cart items after successful deletion
                        val updatedCartItems = cartData.cartItem.values.toList()
                        Resources.Success(updatedCartItems)
                    } else {
                        // Product not found in the cart
                        Resources.Failure(Exception("Product not found in the cart"))
                    }
                } else {
                    // Cart document does not exist
                    Resources.Failure(Exception("Cart document does not exist"))
                }
            } else {
                // Current user is null
                Resources.Failure(Exception("Current user is null"))
            }
        } catch (e: Exception) {
            // Handle any exceptions
            Resources.Failure(e)
        }
    }

    override suspend fun getDeliveryAddress(userId: String): Resources<List<Address>> {
        return try {
            val addressDoc = db.collection("address").document(userId)
            val snapshot = addressDoc.get().await()
            if (snapshot.exists()) {
                val addressList = mutableListOf<Address>()
                val data = snapshot.toObject<Address>()
                data?.let { addressList.add(it) }
                return Resources.Success(addressList)
            } else {
                Resources.Failure(Exception("Address not found"))
            }
        } catch (e: Exception) {
            Log.d("TAG", "getDeliveryAddress: ${e.message}")
            Resources.Failure(Exception("Error fetching address: ${e.message}"))
        }
    }

    override suspend fun saveAddress(address: Address, userId: String): Resources<String> {
        val addressDoc = db.collection("address").document(userId)
        return try {
            addressDoc.set(address).await()
            Resources.Success("Add address success!")
        } catch (e: Exception) {
            Resources.Failure(e)
        }
    }

    override suspend fun saveOrder(order: Order, userId: String): Resources<String> {
        val orderDoc = db.collection("order").document(userId)
        return try {
            // Get the existing OrderList document
            val existingOrderListSnapshot = orderDoc.get().await()
            val existingOrderList = existingOrderListSnapshot.toObject(OrderList::class.java)

            // Create a new OrderList or update the existing one with the new order
            val newOrderList =
                existingOrderList?.copy(orderList = existingOrderList.orderList + (order.orderId to order))
                    ?: OrderList(mutableMapOf(order.orderId to order))

            // Set the new OrderList document
            orderDoc.set(newOrderList).await()

            Resources.Success("Added to cart successfully")
        } catch (e: FirebaseFirestoreException) {
            Resources.Failure(Exception(e.message))
        } catch (e: Exception) {
            Resources.Failure(Exception(e.message))
        }
    }

    override suspend fun getOrders(userId: String): Resources<OrderList> {
        val orderDoc = db.collection("order").document(userId)
        return try {
            val orderListSnapshot = orderDoc.get().await()
            val orderList = orderListSnapshot.toObject(OrderList::class.java)

            if (orderList != null) {
                Resources.Success(orderList)
            } else {
                Resources.Failure(Exception("Order list not found"))
            }
        } catch (e: FirebaseFirestoreException) {
            Resources.Failure(Exception(e.message))
        } catch (e: Exception) {
            Resources.Failure(Exception(e.message))
        }
    }

    override suspend fun cancelOrder(
        order: Order, userId: String, reason: String
    ): Resources<String> {
        val cancelOrderDoc = db.collection("order").document(userId)
        return try {
            val snapshot = cancelOrderDoc.get().await()
            if (snapshot.exists()) {
                val existingItem = snapshot.toObject(OrderList::class.java)
                existingItem?.let {
                    existingItem.orderList[order.orderId]?.cancelled = true
                    existingItem.orderList[order.orderId]?.reasonCancel = reason
                    cancelOrderDoc.set(existingItem).await()
                }
            }
            Resources.Success("Cancel order successfully")
        } catch (e: FirebaseFirestoreException) {
            Resources.Failure(Exception(e.message))
        } catch (e: Exception) {
            Resources.Failure(Exception(e.message))
        }
    }

    override suspend fun deleteOrderInDatabase(cancelOrder: CancelOrder, userId: String) {
        val orderDoc = db.collection("order").document(userId)
        val snapshot = orderDoc.get().await()

        if (snapshot.exists()) {
            val orderData = snapshot.toObject(OrderList::class.java)
            val existingItem = orderData?.orderList?.get(cancelOrder.order.orderId)

            if (existingItem != null) {
                val updateItem = hashMapOf<String, Any>(
                    "orderList.${cancelOrder.order.orderId}" to FieldValue.delete()
                )

                orderDoc.update(updateItem).await()
            }
        }
    }

    override suspend fun sendMessage(message: Message, conversationId: String) {
        // Use withContext to switch to a background thread (Dispatchers.IO)
        withContext(Dispatchers.IO) {
            try {
                if (!message.imageUrl.isNullOrEmpty() && message.message.isNullOrEmpty()) {
                    // Upload image and get image URL
                    val imageUrl = uploadImage(Uri.parse(message.imageUrl))
                    // Set the image URL in the message
                    message.imageUrl = imageUrl.toString()
                }

                val conversationRef = realtimeDatabase.getReference("messages/$conversationId/")

                //add message id (auto generate by firebase)
                val messageId =
                    conversationRef.push().key ?: throw Exception("Failed to generate message ID")

                //set message details
                val messageData = mapOf(
                    "id" to message.id,
                    "senderId" to message.senderId,
                    "senderEmail" to message.senderEmail,
                    "senderName" to message.senderName,
                    "senderPhoto" to message.senderPhoto,
                    "recipient" to message.recipient,
                    "message" to message.message,
                    "imageUrl" to message.imageUrl,
                    "timestamp" to message.timestamp
                )

                // add new message
                conversationRef.child(messageId).setValue(messageData).await()
                updateLastMessageInUser(message)
            } catch (e: Exception) {
                throw Exception("Error sending message: ${e.message}")
            }
        }
    }

    override fun getMessage(userId: String, liveData: MutableLiveData<List<Message>>) {
        val messageRef = realtimeDatabase.getReference("messages/$userId")
        messageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages: List<Message> = snapshot.children.map {
                    it.getValue(Message::class.java)!!.copy(id = snapshot.key!!)
                }
                liveData.postValue(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "onCancelled: $error")
            }

        })
    }

    override suspend fun saveMessageToLocal(message: Message) {
        try {
            appDatabase.messageDao().insertMessage(message)
        } catch (e: SQLiteConstraintException) {
            Log.e("DataRepositoryImp", "Error inserting message: ${e.message}")
        }
    }

    override suspend fun getMessageFromLocal(senderId: String): List<Message> {
        return appDatabase.messageDao().getAllMessages(senderId)
    }

    override suspend fun uploadImage(uri: Uri): Uri {
        val storageRef = storage.reference
        val imagesRef = storageRef.child("chatImages/${UUID.randomUUID()}")
        imagesRef.putFile(uri).await()

        return imagesRef.downloadUrl.await()
    }

    private suspend fun updateLastMessageInUser(message: Message) {
        try {
            withContext(Dispatchers.IO) {
                val chatUserRef = realtimeDatabase.getReference("chatUser/${message.senderId}")
                chatUserRef.updateChildren(
                    mapOf(
                        "id" to message.id,
                        "senderId" to message.senderId,
                        "senderEmail" to message.senderEmail,
                        "senderName" to message.senderName,
                        "senderPhoto" to message.senderPhoto,
                        "recipient" to message.recipient,
                        "message" to message.message,
                        "imageUrl" to message.imageUrl,
                        "timestamp" to message.timestamp
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("DataRepositoryImp", "Error updating last message: ${e.message}")
        }
    }

}