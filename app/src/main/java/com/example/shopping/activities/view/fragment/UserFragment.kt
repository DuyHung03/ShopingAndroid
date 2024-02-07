package com.example.shopping.activities.view.fragment

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.shopping.R
import com.example.shopping.activities.helper.GlideImageLoader
import com.example.shopping.activities.view.activities.MainActivity
import com.example.shopping.activities.view.auth.LoginActivity
import com.example.shopping.activities.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView


@AndroidEntryPoint
class UserFragment : Fragment() {

    private lateinit var loginButton: Button
    private lateinit var logOutButton: LinearLayout
    private val viewModel by viewModels<AuthViewModel>()
    private lateinit var displayName: TextView
    private lateinit var avatar: CircleImageView
    private lateinit var glideImageLoader: GlideImageLoader

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

    private fun initialView(view: View) {
        loginButton = view.findViewById(R.id.loginButton)
        displayName = view.findViewById(R.id.displayName)
        avatar = view.findViewById(R.id.avatar)
        logOutButton = view.findViewById(R.id.logOutButton)
        glideImageLoader = GlideImageLoader(context)
    }

    private fun initialListener() {
        checkIsUserAvailable()

        loginButton.setOnClickListener {
            toLoginScreen()
        }

        logOutButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Log out")
                .setIcon(R.drawable.ic_launc_foreground)
                .setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { _, _ ->
                    viewModel.logout()
                    redirectToHomeFragment()
                }
                .setNegativeButton("No", null).show()
        }
    }

    private fun toLoginScreen() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(
            intent,
            ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.fade_out)
                .toBundle()
        )
    }

    private fun redirectToHomeFragment() {
        val intent = Intent(context, MainActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(
            context,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        context?.startActivity(intent, options.toBundle())
    }

    private fun checkIsUserAvailable() {
        val user = viewModel.currentUser
        if (user != null) {
            displayName.text = user.displayName
            displayName.visibility = View.VISIBLE
            loginButton.visibility = View.GONE
            logOutButton.visibility = View.VISIBLE
            if (user.photoUrl != null) {
                glideImageLoader.load(
                    user.photoUrl.toString(), avatar, R.drawable.user, R.drawable.user
                )
            }
        } else {
            displayName.visibility = View.GONE
            logOutButton.visibility = View.GONE
        }
    }

}