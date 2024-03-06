package com.example.shopping.activities.entities

data class City(
    val results: List<Result>
) {
    data class Result(
        val province_id: String,
        val province_name: String,
        val province_type: String
    )
}