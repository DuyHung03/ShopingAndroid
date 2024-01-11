package com.example.shopping.activities.view.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.shopping.R
import com.example.shopping.activities.view.auth.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : Fragment() {

    private lateinit var loginButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        initialView(view)
        initialListener()

        return view
    }

    private fun initialListener() {
        loginButton.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent,
                ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.fade_out)
                    .toBundle()
            )
        }

    }

    private fun initialView(view: View) {
        loginButton = view.findViewById(R.id.loginButton)
    }

}