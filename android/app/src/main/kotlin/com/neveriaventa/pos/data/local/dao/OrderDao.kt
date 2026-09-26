package com.neveriaventa.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.neveriaventa.pos.data.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: Order): Long

    @Query("SELECT * FROM orders WHERE isSynced = 0 ORDER BY createdAt DESC")
    fun getUnsyncedOrders(): Flow<List<Order>>

    @Query("UPDATE orders SET isSynced = 1, syncedAt = :t WHERE id = :orderId")
    suspend fun markAsSynced(orderId: Long, t: Long = System.currentTimeMillis())
}
