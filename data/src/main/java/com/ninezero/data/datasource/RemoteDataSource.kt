package com.ninezero.data.datasource

import com.ninezero.data.remote.model.ApiResult
import com.ninezero.data.remote.model.HomeResponse

interface RemoteDataSource {
    suspend fun getHomeData(): ApiResult<HomeResponse>
}