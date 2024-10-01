package com.ninezero.data.repository

import com.ninezero.data.datasource.RemoteDataSource
import com.ninezero.data.mapper.CategoryMapper
import com.ninezero.domain.model.Category
import com.ninezero.domain.model.CategoryDetails
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val categoryMapper: CategoryMapper
) : CategoryRepository {
    override fun fetchCategories(): Flow<EntityWrapper<List<Category>>> =
        remoteDataSource.getCategories().map { apiResult ->
            categoryMapper.mapFromResult(apiResult)
        }

    override fun fetchCategoryDetails(categoryId: String): Flow<EntityWrapper<CategoryDetails>> =
        remoteDataSource.getCategoryDetails(categoryId).map { apiResult ->
            categoryMapper.mapCategoryDetails(apiResult)
        }
}