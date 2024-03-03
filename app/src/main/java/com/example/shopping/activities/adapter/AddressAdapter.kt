package com.example.shopping.activities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.R
import com.example.shopping.activities.entities.Address

class AddressAdapter(private val itemList: List<Address>) :
    RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val address: TextView = itemView.findViewById(R.id.addressDetail)

        fun bind(addressDetails: String) {
            address.text = addressDetails
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = itemList[position]

        val details =
            "${address.name}, " +
                    "${address.phoneNumber}\n${address.city}," +
                    "\n ${address.ward}, ${address.district}, " +
                    "\n${address.houseAddress}"

        holder.bind(details)
    }
}