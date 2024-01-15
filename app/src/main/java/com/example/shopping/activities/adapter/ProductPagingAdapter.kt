package com.example.shopping.activities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.R
import com.example.shopping.activities.entities.Product
import com.example.shopping.activities.helper.GlideImageLoader

class ProductPagingAdapter(
    private val onClickProduct: (Product) -> Unit,
) :
    PagingDataAdapter<Product, ProductPagingAdapter.ViewHolder>(itemComparator) {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.imageView)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val price: TextView = itemView.findViewById(R.id.price)

        fun setData(imageUrl: String, name: String, price: String) {
            GlideImageLoader(itemView.context).load(
                imageUrl, image, R.drawable.background, R.drawable.background
            )
            this.productName.text = name
            this.price.text = price
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)
        if (product != null) {
            holder.setData(product.images[0], product.title, product.price.toString())
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    companion object {
        private val itemComparator = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }

        }
    }
}