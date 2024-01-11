package com.example.shopping.activities.helper

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class KeyboardUtils {
    fun clearFocus(inputList: List<EditText?>) {
        for (input in inputList) {
            input!!.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    input.clearFocus()
                    hideKeyboard(input)
                }
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}