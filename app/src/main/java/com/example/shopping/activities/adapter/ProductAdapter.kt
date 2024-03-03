package com.example.shopping.activities.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.R
import com.example.shopping.activities.entities.Product

class ProductAdapter(
    private var oldProductList: List<Product>,
    private val onItemClicked: (Product) -> Unit,
) : RecyclerView.Adapter<ProductViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return oldProductList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = oldProductList[position]
        holder.setData(product.images[0], product.title, product.price.toString())

        holder.itemView.setOnClickListener {
            onItemClicked(product)
        }
    }

    fun setData(newProductList: List<Product>) {
        val diffUtil = ProductDiffUtil(oldProductList, newProductList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldProductList = newProductList
        diffResult.dispatchUpdatesTo(this)
    }

}