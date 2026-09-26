package com.neveriaventa.pos.data.repository

import com.neveriaventa.pos.data.local.dao.ProductDao
import com.neveriaventa.pos.data.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepository @Inject constructor(private val productDao: ProductDao) {
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()
    suspend fun refreshProducts(products: List<Product>) {
        productDao.clearAll()
        productDao.insertProducts(products)
    }
}
