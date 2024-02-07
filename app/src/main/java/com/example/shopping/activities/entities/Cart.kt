package com.example.shopping.activities.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
//    val cartId: String? = Random().nextLong().toString(),
    val cartItem: Map<String, CartItem> = emptyMap()
) : Parcelable {
    // Add a no-argument constructor for Firestore deserialization
    constructor() : this(emptyMap())
}

@Parcelize
data class CartItem(
    val productId: String = "",
    val product: Product,
    var quantity: Int
) : Parcelable {
    // Add a no-argument constructor for Firestore deserialization
    constructor() : this("", Product(), 0)
}
