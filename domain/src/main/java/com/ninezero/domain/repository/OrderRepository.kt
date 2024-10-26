package com.ninezero.domain.repository

import com.ninezero.domain.model.Order
import com.ninezero.domain.model.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun createOrder(order: Order): Result<String>
    fun getOrders(userId: String): Flow<List<Order>>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit>
}