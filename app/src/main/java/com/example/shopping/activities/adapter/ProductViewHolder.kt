package com.example.shopping.activities.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.R
import com.example.shopping.activities.helper.GlideImageLoader

 class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val image: ImageView = itemView.findViewById(R.id.imageView)
    private val name: TextView = itemView.findViewById(R.id.productName)
    private val price: TextView = itemView.findViewById(R.id.price)

    fun setData(imageUrl: String, name: String, price: String) {
        GlideImageLoader(itemView.context).load(
            imageUrl, image, R.drawable.spinner_loading, R.drawable.image_placeholder
        )
        this.name.text = name
        this.price.text = price
    }
}