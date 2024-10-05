package com.ninezero.data.repository

import com.ninezero.data.db.dao.CartDao
import com.ninezero.data.db.entity.toCartProductEntity
import com.ninezero.data.db.entity.toDomain
import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
) : CartRepository {
    override fun fetchAll(): Flow<List<Product>> =
        cartDao.fetchAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addToCart(product: Product) = cartDao.insert(product.toCartProductEntity())

    override suspend fun removeFromCart(productId: String) = cartDao.delete(productId)

    override suspend fun updateSelection(productId: String, isSelected: Boolean) =
        cartDao.updateSelection(productId, isSelected)

    override suspend fun updateAllSelection(isSelected: Boolean) =
        cartDao.updateAllSelection(isSelected)

    override fun isInCart(productId: String): Flow<Boolean> = cartDao.isInCart(productId)
}