package com.example.shopping.activities.helper

import com.wajahatkarim3.easyvalidation.core.view_ktx.validator

class InputValidationHelper {
    fun isValidEmail(email: String, callback: (String) -> Unit): Boolean {
        return email.validator()
            .nonEmpty()
            .validEmail()
            .addErrorCallback {
                callback(it)
            }
            .check()
    }

    fun isValidPassword(password: String, callback: (String) -> Unit): Boolean {
        return password.validator()
            .nonEmpty()
            .minLength(6)
            .addErrorCallback {
                callback(it)
            }
            .check()
    }

    fun isMatchPassword(
        password: String,
        confirmPassword: String,
        callback: (String) -> Unit
    ): Boolean {
        return if (confirmPassword == password) {
            true
        } else {
            callback("Password doesn't match!")
            false
        }
    }

    fun isValidName(
        name: String,
        callback: (String) -> Unit
    ): Boolean {
        return name.validator()
            .nonEmpty()
            .minLength(6)
            .addErrorCallback {
                callback(it)
            }
            .check()
    }

    fun isValidAddress(
        address: String,
        callback: (String) -> Unit
    ): Boolean {
        return address.validator()
            .nonEmpty()
            .minLength(10)
            .addErrorCallback {
                callback(it)
            }
            .check()
    }


    fun isVietnamesePhoneNumber(number: String, callback: (String) -> Unit): Boolean {
        val regex = """([\+84|84|0]+(3|5|7|8|9|1[2|6|8|9]))+([0-9]{8})\b""".toRegex()
        return if (regex.matches(number)) {
            true
        } else {
            callback("Invalid phone number")
            false
        }
    }

}