package com.example.shopping.activities.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val category: Category = Category(), // Provide a default value
    val creationAt: String = "",
    val description: String = "",
    val id: Int = 0,
    val images: List<String> = emptyList(),
    val price: Int = 0,
    val title: String = "",
    val updatedAt: String = ""
) : Parcelable {
    // Add a no-argument constructor for Firestore deserialization
    constructor() : this(Category())
}