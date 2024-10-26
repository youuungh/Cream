package com.ninezero.domain.usecase

import com.ninezero.domain.model.Order
import com.ninezero.domain.model.OrderStatus
import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authUseCase: AuthUseCase
) {
    suspend fun createOrder(products: List<Product>, totalAmount: Int): Result<String> {
        val currentUser = authUseCase.getCurrentUser()!!

        val order = Order(
            userId = currentUser.id,
            products = products,
            totalAmount = totalAmount,
            status = OrderStatus.PENDING
        )
        return orderRepository.createOrder(order)
    }

    fun getOrders(): Flow<List<Order>> = flow {
        val currentUser = authUseCase.getCurrentUser()!!
        orderRepository.getOrders(currentUser.id).collect { emit(it)}
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> =
        orderRepository.updateOrderStatus(orderId, status)
}