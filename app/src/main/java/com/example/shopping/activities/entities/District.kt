package com.example.shopping.activities.entities

data class District(
    val results: List<Result>
) {
    data class Result(
        val district_id: String,
        val district_name: String,
        val district_type: String,
        val province_id: String
    )
}