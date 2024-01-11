package com.example.shopping.activities.utils

sealed class Resources<out R> {

    data class Success<out R>(val result: R) : Resources<R>()
    data class Failure(val e: Exception) : Resources<Nothing>()
     object Loading:Resources<Nothing>()
}