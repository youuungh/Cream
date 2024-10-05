package com.ninezero.data.repository

import com.ninezero.data.db.dao.SaveDao
import com.ninezero.data.db.entity.toDomain
import com.ninezero.data.db.entity.toSavedProductEntity
import com.ninezero.domain.model.Product
import com.ninezero.domain.repository.SaveRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SaveRepositoryImpl @Inject constructor(
    private val saveDao: SaveDao
) : SaveRepository {
    override fun fetchAll(): Flow<List<Product>> =
        saveDao.fetchAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveProduct(product: Product) = saveDao.insert(product.toSavedProductEntity())

    override suspend fun removeFromSaved(productId: String) = saveDao.delete(productId)

    override suspend fun removeAll() = saveDao.deleteAll()

    override fun isSaved(productId: String): Flow<Boolean> = saveDao.isSaved(productId)
}