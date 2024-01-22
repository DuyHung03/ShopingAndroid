package com.example.shopping.activities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.R
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.helper.GlideImageLoader

class ProductAdapter(
    private val itemList: List<Product>,
    private val onItemClicked: (Product) -> Unit,
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.imageView)
        private val name: TextView = itemView.findViewById(R.id.productName)
        private val price: TextView = itemView.findViewById(R.id.price)

        fun setData(imageUrl: String, name: String, price: String) {
            GlideImageLoader(itemView.context).load(
                imageUrl, image, R.drawable.spinner_loading, R.drawable.background
            )
            this.name.text = name
            this.price.text = price
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = itemList[position]
        holder.setData(product.images[0], product.title, product.price.toString())

        holder.itemView.setOnClickListener {
            onItemClicked(product)
        }
    }
}