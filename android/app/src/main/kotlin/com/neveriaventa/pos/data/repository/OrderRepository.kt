package com.neveriaventa.pos.data.repository

import com.neveriaventa.pos.data.local.dao.OrderDao
import com.neveriaventa.pos.data.model.Order
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OrderRepository @Inject constructor(private val orderDao: OrderDao) {
    fun getUnsyncedOrders(): Flow<List<Order>> = orderDao.getUnsyncedOrders()
    suspend fun saveOrder(order: Order): Long = orderDao.insertOrder(order)
    suspend fun markAsSynced(orderId: Long) = orderDao.markAsSynced(orderId)
}
