package com.example.shopping.activities.view.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping.R
import com.example.shopping.activities.adapter.AddressAdapter
import com.example.shopping.activities.entities.Address
import com.example.shopping.activities.utils.Resources
import com.example.shopping.activities.utils.Toast.toast
import com.example.shopping.activities.viewmodel.AuthViewModel
import com.example.shopping.activities.viewmodel.DataViewModel
import com.example.shopping.databinding.ActivityAddressListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressListBinding
    private val authViewModel by viewModels<AuthViewModel>()
    private val dataViewModel by viewModels<DataViewModel>()
    private val addressList = mutableListOf<Address>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeListener()
    }

    private fun initializeListener() {
        binding.backButton.setOnClickListener {
            this.finish()
        }

        binding.addNewAddress.setOnClickListener {
            toAddNewAddressScreen()
        }

        dataViewModel.getDeliveryAddress(authViewModel.currentUser!!.uid)
        dataViewModel.addressFlow.observe(this) { state ->
            when (state) {
                is Resources.Failure -> {
                    toast(state.e.message.toString())
                    binding.progressBar.visibility = View.GONE
                }

                Resources.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resources.Success -> {
                    val res = state.result
                    addressList.clear()
                    addressList.addAll(res)
                    binding.progressBar.visibility = View.GONE
                }
            }
            setupRecyclerView()
        }
    }

    override fun onResume() {
        super.onResume()
        dataViewModel.getDeliveryAddress(authViewModel.currentUser!!.uid)

    }

    private fun toAddNewAddressScreen() {
        val intent = Intent(this, AddNewAddressActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        this.startActivity(intent, options.toBundle())
    }

    private fun setupRecyclerView() {
        val addressAdapter = AddressAdapter(addressList)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.addressRecyclerView.layoutManager = layoutManager
        binding.addressRecyclerView.adapter = addressAdapter
    }
}