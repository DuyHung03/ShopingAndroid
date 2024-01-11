package com.example.shopping.activities.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId: String,
    val email: String?,
    val password: String?,
    val photoUrl: String?,
) : Parcelable
