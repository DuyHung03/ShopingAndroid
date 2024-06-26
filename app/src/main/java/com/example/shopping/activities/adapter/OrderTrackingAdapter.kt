package com.example.shopping.activities.adapter

import android.annotation.SuppressLint
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
        val status = itemView.findViewById<TextView>(R.id.status)!!
        private val total = itemView.findViewById<TextView>(R.id.total)
        private val totalPrice = itemView.findViewById<TextView>(R.id.totalPrice)
        private val orderId = itemView.findViewById<TextView>(R.id.orderId)
        private val reasonTv = itemView.findViewById<TextView>(R.id.reason)

        @SuppressLint("SetTextI18n")
        fun bind(
            imageUrl: String,
            name: String,
            confirmed: Boolean,
            cancelled: Boolean,
            reason: String?,
            itemCount: String,
            price: String,
            id: String
        ) {
            GlideImageLoader(itemView.context).load(
                imageUrl, image, R.drawable.image_placeholder, R.drawable.image_placeholder
            )

            productName.text = name
            if (confirmed && !cancelled) {
                status.text = "Confirmed"
            }

            if (!confirmed && !cancelled) {
                status.text = "Waiting for confirmed"
            }

            if (cancelled) {
                status.text = "Cancelled"
            }

            total.text = "Total ($itemCount): "
            totalPrice.text = "$$price"
            orderId.text = id

            reason?.let {
                reasonTv.visibility = View.VISIBLE
                reasonTv.text = reason
            }
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
            order.cancelled,
            order.reasonCancel,
            order.productList.size.toString(),
            order.cost.total.toString(),
            order.orderId
        )

        if (order.confirmed && !order.cancelled) {
            holder.status.setTextColor(
                holder.itemView.context.getColor(R.color.green)
            )
        }
        if (!order.confirmed && !order.cancelled) {
            holder.status.setTextColor(
                holder.itemView.context.getColor(R.color.orange)
            )
        }
        if (order.cancelled) {
            holder.status.setTextColor(
                holder.itemView.context.getColor(R.color.red)
            )
        }

        holder.itemView.setOnClickListener {
            onItemClicked(order)
        }

    }
}