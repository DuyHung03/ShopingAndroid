package com.example.shopping.activities.view.activities

import android.os.Bundle
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

        dataViewModel.getDeliveryAddress(authViewModel.currentUser!!.uid)

        dataViewModel.addressFlow.observe(this) { state ->
            when (state) {
                is Resources.Failure -> {
                    toast(state.e.message.toString())
                }

                Resources.Loading -> {

                }

                is Resources.Success -> {
                    val res = state.result
                    addressList.addAll(res)
                }
            }
        }
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        val addressAdapter = AddressAdapter(addressList)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.addressRecyclerView.layoutManager = layoutManager
        binding.addressRecyclerView.adapter = addressAdapter
    }
}