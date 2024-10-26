package com.ninezero.domain.model

import java.util.Date

data class Order(
    val orderId: String = "",
    val userId: String,
    val products: List<Product>,
    val totalAmount: Int,
    val status: OrderStatus,
    val orderDate: Date = Date()
)

enum class OrderStatus {
    PENDING, PREPARING, IN_TRANSIT, DELIVERED
}
