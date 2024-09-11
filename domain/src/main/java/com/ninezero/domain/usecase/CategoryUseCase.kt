package com.ninezero.domain.usecase

import com.ninezero.domain.model.Category
import com.ninezero.domain.model.CategoryDetails
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<EntityWrapper<List<Category>>> {
        return categoryRepository.fetchCategories()
    }

    fun getCategoryDetails(categoryId: String): Flow<EntityWrapper<CategoryDetails>> {
        return categoryRepository.fetchCategoryDetails(categoryId)
    }
}