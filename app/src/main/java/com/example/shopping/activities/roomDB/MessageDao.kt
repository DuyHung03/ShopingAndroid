package com.example.shopping.activities.roomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.shopping.activities.entities.Message

@Dao
interface MessageDao {
    @Insert
    suspend fun insertMessage(message: Message)

    @Query("SELECT * FROM messages WHERE senderId = :senderId ORDER BY timestamp ASC")
    suspend fun getAllMessages(senderId: String): List<Message>
}
