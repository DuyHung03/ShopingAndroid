package com.example.shopping.activities.helper

import com.example.shopping.activities.entities.User
import com.google.gson.Gson

class JsonConverter {
    companion object {
        private val gson = Gson()
        fun convertToJson(user: User): String {
            return gson.toJson(user)
        }

        fun convertFromJson(json: String): User {
            return gson.fromJson(json, User::class.java)
        }
    }
}