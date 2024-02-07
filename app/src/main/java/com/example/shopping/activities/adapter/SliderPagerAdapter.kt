package com.example.shopping.activities.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shopping.R
import com.example.shopping.activities.helper.GlideImageLoader

class SliderPagerAdapter(private val imageList: List<String>) :
    RecyclerView.Adapter<SliderPagerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.imageView)


        fun setImage(imageUrl: String) {
            GlideImageLoader(itemView.context).load(
                imageUrl, image, R.drawable.spinner_loading, R.drawable.image_placeholder
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.banner_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setImage(imageList[position])
    }
}