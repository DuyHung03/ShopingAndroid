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

}