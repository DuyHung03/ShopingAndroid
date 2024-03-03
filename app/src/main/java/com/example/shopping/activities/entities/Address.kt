package com.example.shopping.activities.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Address(
    val name: String,
    val phoneNumber: Long,
    val city: String,
    val district: String,
    val ward: String,
    val houseAddress: String,
    val isDefault: Boolean
) : Parcelable