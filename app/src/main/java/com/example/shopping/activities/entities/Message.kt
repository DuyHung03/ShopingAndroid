package com.example.shopping.activities.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "messages")
@Parcelize
data class Message(
    @PrimaryKey
    val id: String,
    val senderId: String,
    val senderEmail: String,
    val senderName: String?,
    val senderPhoto: String?,
    val recipient: String,
    val message: String?,
    var imageUrl: String?,
    val timestamp: Long
) : Parcelable{
    constructor() : this("", "", "", null, null, "", null, null, 0)

}