package com.neveriaventa.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neveriaventa.pos.data.model.Order
import com.neveriaventa.pos.data.model.OrderItem
import com.neveriaventa.pos.data.model.Product
import com.neveriaventa.pos.data.repository.OrderRepository
import com.neveriaventa.pos.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PosViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    val products: Flow<List<Product>> = productRepository.getAllProducts()

    private val _cart = MutableStateFlow<List<OrderItem>>(emptyList())
    val cart = _cart.asStateFlow()

    val total: StateFlow<Double> = _cart.map { items -> items.sumOf { it.price * it.quantity } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    init {
        // Datos de prueba (borrar cuando conectes a Laravel)
        viewModelScope.launch {
            productRepository.refreshProducts(
                listOf(
                    Product(1, "Cafe Americano", 2.50, "Bebidas"),
                    Product(2, "Cappuccino", 3.00, "Bebidas"),
                    Product(3, "Latte", 3.25, "Bebidas"),
                    Product(4, "Te Verde", 2.00, "Bebidas"),
                    Product(5, "Chocolate", 2.75, "Bebidas"),
                    Product(6, "Agua", 1.00, "Bebidas"),
                    Product(7, "Donut", 1.50, "Alimentos"),
                    Product(8, "Croissant", 2.00, "Alimentos"),
                    Product(9, "Sandwich", 4.50, "Alimentos"),
                    Product(10, "Muffin", 2.25, "Alimentos"),
                    Product(11, "Galleta", 1.25, "Alimentos"),
                    Product(12, "Brownie", 2.50, "Alimentos")
                )
            )
        }
    }

    fun addToCart(product: Product) {
        val current = _cart.value.toMutableList()
        val existing = current.find { it.productId == product.id }
        if (existing != null) existing.quantity++ else
            current.add(OrderItem(product.id, product.name, product.price))
        _cart.value = current
    }

    fun removeFromCart(item: OrderItem) {
        _cart.value = _cart.value.filter { it.productId != item.productId }
    }

    fun checkout() {
        viewModelScope.launch {
            val items = _cart.value
            if (items.isEmpty()) return@launch
            val total = items.sumOf { it.price * it.quantity }
            orderRepository.saveOrder(
                Order(
                    orderNumber = "ORD-${UUID.randomUUID().toString().take(8).uppercase()}",
                    total = total,
                    createdAt = System.currentTimeMillis(),
                    isSynced = false
                )
            )
            _cart.value = emptyList()
        }
    }
}
