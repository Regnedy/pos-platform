package com.neveriaventa.pos.di

import android.content.Context
import com.neveriaventa.pos.data.local.dao.OrderDao
import com.neveriaventa.pos.data.local.dao.ProductDao
import com.neveriaventa.pos.data.local.db.PosDatabase
import com.neveriaventa.pos.data.repository.OrderRepository
import com.neveriaventa.pos.data.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton @Provides
    fun provideDb(@ApplicationContext ctx: Context): PosDatabase = PosDatabase.getInstance(ctx)

    @Singleton @Provides
    fun provideProductDao(db: PosDatabase): ProductDao = db.productDao()

    @Singleton @Provides
    fun provideOrderDao(db: PosDatabase): OrderDao = db.orderDao()

    @Singleton @Provides
    fun provideProductRepository(dao: ProductDao): ProductRepository = ProductRepository(dao)

    @Singleton @Provides
    fun provideOrderRepository(dao: OrderDao): OrderRepository = OrderRepository(dao)
}
