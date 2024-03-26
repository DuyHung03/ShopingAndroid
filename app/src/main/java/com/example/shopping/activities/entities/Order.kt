package com.example.shopping.activities.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class OrderList(
    val orderList: Map<String, Order> = emptyMap()
) : Parcelable {
    constructor() : this(emptyMap())
}

@Parcelize
data class Order(
    val orderId: String,
    val customerId: String,
    val address: Address,
    val productList: ArrayList<CartItem>,
    val cost: Cost,
    val createdTime: Date,
    var confirmed: Boolean,
    var cancelled: Boolean = false,
    var reasonCancel: String? = null
) : Parcelable {
    constructor() : this("", "", Address(), ArrayList(), Cost(), Date(), false)
}
