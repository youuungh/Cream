package com.ninezero.domain.repository

import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.HomeData
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun fetchHomeData(): Flow<EntityWrapper<HomeData>>
}