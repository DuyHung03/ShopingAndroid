package com.example.shopping.activities.view

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.shopping.R

class SearchActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initialView()
        initialListener()
    }

    private fun initialView() {
        backButton = findViewById(R.id.backButton)
        searchView = findViewById(R.id.searchView)
    }

    private fun initialListener() {
        backButton.setOnClickListener {
            this.finish()
        }

        searchView.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                searchView.isIconified = false
                searchView.requestFocus()
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT)
            }
        }

    }
}