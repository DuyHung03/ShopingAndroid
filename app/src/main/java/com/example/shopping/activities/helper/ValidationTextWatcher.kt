package com.example.shopping.activities.helper

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout

class ValidationTextWatcher(private val inputLayoutList: List<TextInputLayout>) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        for (inputLayout in inputLayoutList) {

            inputLayout.error = null
            inputLayout.setHelperTextColor(null)

        }
    }
}