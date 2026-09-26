package com.neveriaventa.pos.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "products")
@Serializable
data class Product(
    @PrimaryKey val id: Long,
    val name: String,
    val price: Double,
    val category: String,
    val imageUrl: String = "",
    val isActive: Boolean = true
)

@Entity(tableName = "orders")
@Serializable
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String = "",
    val total: Double = 0.0,
    val paymentMethod: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val syncedAt: Long? = null
)

@Serializable
data class OrderItem(
    val productId: Long,
    val name: String,
    val price: Double,
    var quantity: Int = 1,
    val size: String = "M"
) {
    val subtotal: Double get() = price * quantity
}
