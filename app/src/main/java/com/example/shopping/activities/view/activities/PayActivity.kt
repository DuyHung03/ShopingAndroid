package com.example.shopping.activities.view.activities

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping.R
import com.example.shopping.activities.adapter.ProductInPayAdapter
import com.example.shopping.activities.entities.Address
import com.example.shopping.activities.entities.CartItem
import com.example.shopping.activities.entities.Cost
import com.example.shopping.activities.entities.Order
import com.example.shopping.activities.utils.Resources
import com.example.shopping.activities.utils.Toast.toast
import com.example.shopping.activities.viewmodel.AuthViewModel
import com.example.shopping.activities.viewmodel.DataViewModel
import com.example.shopping.databinding.ActivityPayBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import java.util.UUID

@AndroidEntryPoint
class PayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPayBinding
    private var checkedList: ArrayList<CartItem>? = null
    private val dataViewModel by viewModels<DataViewModel>()
    private lateinit var addressDetails: TextView
    private val authViewModel by viewModels<AuthViewModel>()
    private lateinit var address: Address
    private lateinit var cost: Cost
    private lateinit var order: Order
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)

        binding = ActivityPayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkedList?.clear()
        checkedList = intent.getParcelableArrayListExtra("checkedList")!!

        addressDetails = findViewById(R.id.addressDetail)

        initializeListener()

    }

    override fun onResume() {
        super.onResume()
        dataViewModel.getDeliveryAddress(authViewModel.currentUser?.uid!!)
    }

    @SuppressLint("SetTextI18n")
    private fun initializeListener() {

        dataViewModel.getDeliveryAddress(authViewModel.currentUser?.uid!!)
        dataViewModel.addressFlow.observe(this) { state ->
            when (state) {
                is Resources.Failure -> {

                }

                Resources.Loading -> {
                }

                is Resources.Success -> {
                    address = state.result[0]
                    val formattedAddress = SpannableStringBuilder()
                    formattedAddress.append(Html.fromHtml("<b>${address.name}</b>"))
                    formattedAddress.append(",  ${address.phoneNumber}\n")
                    formattedAddress.append("${address.city},\n")
                    formattedAddress.append("${address.ward},\n")
                    formattedAddress.append("${address.district},\n")
                    formattedAddress.append(address.houseAddress)

                    addressDetails.text = formattedAddress
                }
            }
        }

        setupRecyclerView()

        binding.backButton.setOnClickListener {
            this.finish()
        }

        binding.address.setOnClickListener {
            toAddressScreen()
        }

        binding.payButton.setOnClickListener {
            order =
                Order(UUID.randomUUID().toString(), address, checkedList!!, cost, Date(), false)
            dataViewModel.saveOrder(order, authViewModel.currentUser?.uid!!)
        }

        dataViewModel.saveOrderFlow.observe(this) { state ->
            when (state) {
                is Resources.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    toast("${state.e.message}")
                }

                Resources.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resources.Success -> {
                    toResultScreen(order)
                    this.finish()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        setupPriceDetail()

    }

    private fun toResultScreen(order: Order) {
        val intent = Intent(this, ResultOrderActivity::class.java)
        intent.putExtra("order", order)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        this.startActivity(intent, options.toBundle())
    }


    private fun toAddressScreen() {
        val intent = Intent(this, AddNewAddressActivity::class.java)
        intent.putExtra("address", address)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        this.startActivity(intent, options.toBundle())
    }

    private fun setupPriceDetail() {
        var productsCost = 0.0
        val shippingCost = 10.0
        for (item in checkedList!!) {
            productsCost += (item.quantity * item.product.price)
        }

        val total = productsCost + shippingCost

        cost = Cost(productsCost, shippingCost, total)

        binding.totalOfList.text = getString(R.string.price, productsCost.toString())
        binding.shippingCost.text = getString(R.string.price, shippingCost.toString())
        binding.totalTitle.text = getString(R.string.total_title, checkedList!!.size.toString())
        binding.total.text = getString(R.string.price, total.toString())
        binding.totalPrice.text = getString(R.string.price, total.toString())
    }

    private fun setupRecyclerView() {
        if (checkedList != null) {
            val productInPayAdapter = ProductInPayAdapter(checkedList!!)
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.productRecyclerView.layoutManager = layoutManager
            binding.productRecyclerView.adapter = productInPayAdapter
        }
    }


}