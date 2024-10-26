package com.ninezero.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ninezero.data.remote.model.OrderDto
import com.ninezero.data.remote.model.toDomain
import com.ninezero.data.remote.model.toDto
import com.ninezero.domain.model.Order
import com.ninezero.domain.model.OrderStatus
import com.ninezero.domain.repository.OrderRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val fStore: FirebaseFirestore
) : OrderRepository {
    private val ordersCollection = fStore.collection("orders")

    override suspend fun createOrder(order: Order): Result<String> = try {
        val orderDto = order.toDto()
        val documentRef = ordersCollection.document()
        val orderWithId = orderDto.copy(orderId = documentRef.id)
        documentRef.set(orderWithId).await()
        Result.success(documentRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getOrders(userId: String): Flow<List<Order>> = callbackFlow {
        val subscription = ordersCollection
            .whereEqualTo("userId", userId)
            .orderBy("orderDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    close(error)
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull {
                    it.toObject(OrderDto::class.java)?.toDomain()
                } ?: emptyList()
                trySend(orders)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> = try {
        ordersCollection.document(orderId)
            .update("status", status.name)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}