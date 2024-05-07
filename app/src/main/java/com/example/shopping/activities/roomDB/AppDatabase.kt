package com.example.shopping.activities.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shopping.activities.entities.Message

@Database(entities = [Message::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}