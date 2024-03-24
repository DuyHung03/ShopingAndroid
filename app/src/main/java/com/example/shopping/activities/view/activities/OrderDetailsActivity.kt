package com.example.shopping.activities.view.activities

import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping.R
import com.example.shopping.activities.adapter.ProductInPayAdapter
import com.example.shopping.activities.entities.Order
import com.example.shopping.activities.utils.Resources
import com.example.shopping.activities.utils.Toast.toast
import com.example.shopping.activities.viewmodel.AuthViewModel
import com.example.shopping.activities.viewmodel.DataViewModel
import com.example.shopping.databinding.ActivityOrderDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var order: Order
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var addressDetails: TextView
    private val dataViewModel by viewModels<DataViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent != null) {
            order = intent.getParcelableExtra("order")!!
        }

        addressDetails = findViewById(R.id.addressDetail)
        initializeListener()

    }

    private fun initializeListener() {
        if (!order.confirmed && !order.cancelled) {
            binding.cancelButton.visibility = View.VISIBLE
        } else
            binding.cancelButton.visibility = View.GONE

        binding.backButton.setOnClickListener {
            this.finish()
        }

        setContent()

        binding.cancelButton.setOnClickListener {
            showCancelReasonBottomSheet()
        }

        dataViewModel.cancelOrderFlow.observe(this) { state ->
            when (state) {
                is Resources.Failure -> {
                    toast(state.e.message.toString())
                }

                Resources.Loading -> {}
                is Resources.Success -> {
                    toast(state.result)
                    this.finish()
                }
            }
        }

    }

    private fun showCancelReasonBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.cancel_reason_bottom_sheet, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.setCancelable(true)

        setContentToSheet(dialogView)

        bottomSheetDialog.show()
    }

    private fun setContentToSheet(dialogView: View?) {
        dialogView?.let {
            val cancelButton = it.findViewById<MaterialButton>(R.id.cancel_button)
            val radioGroup = it.findViewById<RadioGroup>(R.id.radioGroup)

            cancelButton.setOnClickListener {
                val checked = radioGroup.checkedRadioButtonId != -1
                if (checked) {
                    val selectedButton = radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                    val reasonText = selectedButton.text.toString()

                    if (reasonText.isNotEmpty()) {
                        dataViewModel.cancelOrder(
                            order,
                            authViewModel.currentUser?.uid!!,
                            reasonText
                        )
                    } else {
                        toast("Please choose your reason for cancellation")
                    }
                } else {
                    toast("Please choose your reason for cancellation")
                }
            }
        }
    }


    private fun setContent() {
        //set address details
        val formattedAddress = SpannableStringBuilder()
        formattedAddress.append(Html.fromHtml("<b>${order.address.name}</b>"))
        formattedAddress.append(",  ${order.address.phoneNumber}\n")
        formattedAddress.append("${order.address.city},\n")
        formattedAddress.append("${order.address.ward},\n")
        formattedAddress.append("${order.address.district},\n")
        formattedAddress.append(order.address.houseAddress)

        addressDetails.text = formattedAddress

        //orderId
        binding.orderId.text = order.orderId

        //product list
        setupProductsRecyclerView()

        //cost
        binding.totalOfList.text = getString(R.string.price, order.cost.productsCost.toString())
        binding.shippingCost.text = getString(R.string.price, order.cost.shippingCost.toString())
        binding.promotion.text = getString(R.string.price, "10")
        binding.total.text = getString(R.string.price, order.cost.total.toString())
    }

    private fun setupProductsRecyclerView() {
        val productInPayAdapter = ProductInPayAdapter(order.productList)
        binding.productRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.productRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.productRecyclerView.adapter = productInPayAdapter
    }
}