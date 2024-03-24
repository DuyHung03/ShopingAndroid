package com.example.shopping.activities.view.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.R
import com.example.shopping.activities.adapter.OrderTrackingAdapter
import com.example.shopping.activities.entities.Order
import com.example.shopping.activities.view.activities.OrderDetailsActivity
import com.example.shopping.activities.viewmodel.AuthViewModel
import com.example.shopping.activities.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CancelledFragment : Fragment() {
    private val dataViewModel by viewModels<DataViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var orderTrackingAdapter: OrderTrackingAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cancelled, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        dataViewModel.getOrders(authViewModel.currentUser?.uid!!)
        dataViewModel.cancelledList.observe(viewLifecycleOwner) { cancelList ->
            progressBar.visibility = View.VISIBLE
            cancelList?.let {
                setupRecyclerView(cancelList)
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerView(cancelList: List<Order>) {
        orderTrackingAdapter = OrderTrackingAdapter(cancelList) { order ->
            toOrderDetails(order)
        }
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.adapter = orderTrackingAdapter
    }

    private fun toOrderDetails(order: Order) {
        val intent = Intent(context, OrderDetailsActivity::class.java)
        intent.putExtra("order", order)
        val options = ActivityOptions.makeCustomAnimation(
            context,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
        )
        this.startActivity(intent, options.toBundle())
    }

}