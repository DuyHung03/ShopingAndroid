package com.example.shopping.activities.utils

import android.app.Activity
import android.widget.Toast

object Toast {

    fun Activity.toast(msg:String){
        Toast.makeText(this , msg , Toast.LENGTH_SHORT).show()
    }

}