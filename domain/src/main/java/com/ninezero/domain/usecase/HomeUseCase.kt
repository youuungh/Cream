package com.ninezero.domain.usecase

import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.HomeData
import com.ninezero.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    operator fun invoke(): Flow<EntityWrapper<HomeData>> {
        return homeRepository.fetchHomeData()
    }
}