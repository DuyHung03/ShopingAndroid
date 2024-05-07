package com.example.shopping.activities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.R
import com.example.shopping.activities.entities.CartItem
import com.example.shopping.activities.helper.GlideImageLoader

class ProductInPayAdapter(private val itemList: ArrayList<CartItem>) :
    RecyclerView.Adapter<ProductInPayAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.image)
        val name: TextView = itemView.findViewById(R.id.productName)
        val price: TextView = itemView.findViewById(R.id.price)
        private val quantity: TextView = itemView.findViewById(R.id.quantity)

        fun bind(imageUrl: String, name: String, price: String, quantity: String) {
            GlideImageLoader(itemView.context).load(
                imageUrl, image, R.drawable.image_placeholder, R.drawable.image_placeholder
            )
            this.name.text = name
            this.price.text = "$ $price"
            this.quantity.text = quantity
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_pay, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartProduct = itemList[position]
        holder.bind(
            cartProduct.product.images[0],
            cartProduct.product.title,
            cartProduct.product.price.toString(),
            cartProduct.quantity.toString()
        )
    }
}