package com.ninezero.data.repository

import com.ninezero.data.datasource.RemoteDataSource
import com.ninezero.data.mapper.CategoryMapper
import com.ninezero.domain.model.Category
import com.ninezero.domain.model.CategoryDetails
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val categoryMapper: CategoryMapper
) : CategoryRepository {
    override fun fetchCategories(): Flow<EntityWrapper<List<Category>>> =
        flow {
            emit(categoryMapper.mapFromResult(remoteDataSource.getCategories()))
        }.catch { e ->
            emit(EntityWrapper.Fail(e))
        }

    override fun fetchCategoryDetails(categoryId: String): Flow<EntityWrapper<CategoryDetails>> =
        flow {
            emit(categoryMapper.mapCategoryDetails(remoteDataSource.getCategoryDetails(categoryId)))
        }.catch { e ->
            emit(EntityWrapper.Fail(e))
        }
}