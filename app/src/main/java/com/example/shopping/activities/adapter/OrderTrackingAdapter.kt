package com.example.shopping.activities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.R
import com.example.shopping.activities.entities.Order
import com.example.shopping.activities.helper.GlideImageLoader
import com.makeramen.roundedimageview.RoundedImageView

class OrderTrackingAdapter(
    private val itemList: List<Order>,
    private val onItemClicked: (Order) -> Unit
) :
    RecyclerView.Adapter<OrderTrackingAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image = itemView.findViewById<RoundedImageView>(R.id.image)
        private val productName = itemView.findViewById<TextView>(R.id.productName)
        val status = itemView.findViewById<TextView>(R.id.status)
        private val total = itemView.findViewById<TextView>(R.id.total)
        private val totalPrice = itemView.findViewById<TextView>(R.id.totalPrice)
        private val orderId = itemView.findViewById<TextView>(R.id.orderId)
        fun bind(
            imageUrl: String,
            name: String,
            confirmed: Boolean,
            itemCount: String,
            price: String,
            id: String
        ) {
            GlideImageLoader(itemView.context).load(
                imageUrl, image, R.drawable.spinner_loading, R.drawable.image_placeholder
            )

            productName.text = name
            if (confirmed) {
                status.text = "Confirmed"
            } else {
                status.text = "Waiting for confirmed"
            }
            total.text = "Total ($itemCount): "
            totalPrice.text = "$$price"
            orderId.text = id
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = itemList[position]

        holder.bind(
            order.productList[0].product.images[0],
            order.productList[0].product.title,
            order.confirmed,
            order.productList.size.toString(),
            order.cost.total.toString(),
            order.orderId
        )

        if (order.confirmed) {
            holder.status.setTextColor(
                holder.itemView.context.getColor(R.color.green)
            )
        } else {
            holder.status.setTextColor(
                holder.itemView.context.getColor(R.color.red)
            )
        }

        holder.itemView.setOnClickListener {
            onItemClicked(order)
        }

    }
}