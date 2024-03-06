package com.example.shopping.activities.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.shopping.activities.entities.City
import com.example.shopping.activities.entities.Ward


class WardAdapter(
    context: Context, textViewResourceId: Int,
    values: List<Ward.Result>
) : ArrayAdapter<Any?>(context, textViewResourceId, values) {
    // Your sent context
    private val context: Context

    // Your custom values for the spinner (User)
    private val values: List<Ward.Result>

    init {
        this.context = context
        this.values = values
    }

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): Ward.Result {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as TextView
        label.setTextColor(Color.BLACK)
        label.text = values[position].ward_name
        return label
    }


    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.setTextColor(Color.BLACK)
        label.text = values[position].ward_name
        return label
    }
}