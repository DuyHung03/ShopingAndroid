package com.example.shopping.activities.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CancelList(
    val cancelList: Map<String, CancelOrder> = emptyMap()
) : Parcelable {
    constructor() : this(emptyMap())
}

@Parcelize
class CancelOrder(
    val order: Order,
    val reason: String
) : Parcelable {
    constructor() : this(Order(), "")
}