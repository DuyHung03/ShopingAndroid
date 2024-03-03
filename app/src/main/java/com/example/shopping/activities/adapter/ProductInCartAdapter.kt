package com.example.shopping.activities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.R
import com.example.shopping.activities.entities.CartItem
import com.example.shopping.activities.helper.GlideImageLoader

class ProductInCartAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ProductInCartAdapter.ViewHolder>() {
    private var itemList: List<CartItem> = emptyList()

    interface OnItemClickListener {
        fun onItemClick(item: CartItem)
        fun onCheckBoxClick(isChecked: Boolean, item: CartItem)
        fun onIncreaseQuantity(cartItem: CartItem)

        fun onDeleteItem(cartItem: CartItem)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.image)
        val name: TextView = itemView.findViewById(R.id.productName)
        val price: TextView = itemView.findViewById(R.id.price)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        private val quantity: TextView = itemView.findViewById(R.id.quantity)
        private val increase: ImageView = itemView.findViewById(R.id.increaseButton)
        val decrease: ImageView = itemView.findViewById(R.id.decreaseButton)
        private val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(itemList[bindingAdapterPosition])
            }
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                listener.onCheckBoxClick(isChecked, itemList[bindingAdapterPosition])
            }

            increase.setOnClickListener {
                listener.onIncreaseQuantity(itemList[bindingAdapterPosition])
            }

            deleteButton.setOnClickListener {
                listener.onDeleteItem(itemList[bindingAdapterPosition])
            }
        }

        fun bind(imageUrl: String, name: String, price: String, quantity: String) {
            GlideImageLoader(itemView.context).load(
                imageUrl, image, R.drawable.spinner_loading, R.drawable.image_placeholder
            )
            this.name.text = name
            this.price.text = "$ $price"
            this.quantity.text = quantity
        }
    }

    fun setData(newData: List<CartItem>) {
        val diffCallback = ProductCartDiffCallback(itemList, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        itemList = newData
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_cart, parent, false)
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


    private class ProductCartDiffCallback(
        private val oldList: List<CartItem>,
        private val newList: List<CartItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].productId == newList[newItemPosition].productId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}