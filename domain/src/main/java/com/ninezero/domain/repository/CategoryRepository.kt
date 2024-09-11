package com.ninezero.domain.repository

import com.ninezero.domain.model.Category
import com.ninezero.domain.model.CategoryDetails
import com.ninezero.domain.model.EntityWrapper
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun fetchCategories(): Flow<EntityWrapper<List<Category>>>
    fun fetchCategoryDetails(categoryId: String): Flow<EntityWrapper<CategoryDetails>>
}