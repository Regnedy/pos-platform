package com.neveriaventa.pos.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.neveriaventa.pos.data.local.dao.OrderDao
import com.neveriaventa.pos.data.local.dao.ProductDao
import com.neveriaventa.pos.data.model.Order
import com.neveriaventa.pos.data.model.Product

@Database(entities = [Product::class, Order::class], version = 1, exportSchema = false)
abstract class PosDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile private var INSTANCE: PosDatabase? = null
        fun getInstance(context: Context): PosDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, PosDatabase::class.java, "pos_database")
                    .build().also { INSTANCE = it }
            }
    }
}
