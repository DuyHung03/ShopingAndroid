package com.example.shopping.activities.entities

data class Ward(
    val results: List<Result>
) {
    data class Result(
        val district_id: String,
        val ward_id: String,
        val ward_name: String,
        val ward_type: String
    )
}