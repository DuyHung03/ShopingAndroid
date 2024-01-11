package com.example.shopping.activities.entities

data class Product(
    val category: Category,
    val creationAt: String, // 2024-01-07T22:28:40.000Z
    val description: String, // The automobile layout consists of a front-engine design, with transaxle-type transmissions mounted at the rear of the engine and four wheel drive
    val id: Int, // 2
    val images: List<String>,
    val price: Int, // 353
    val title: String, // Ergonomic Steel Pizza
    val updatedAt: String // 2024-01-07T22:28:40.000Z
)