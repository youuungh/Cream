package com.ninezero.data.datasource

import com.ninezero.data.remote.model.ApiResult
import com.ninezero.data.remote.model.HomeResponse
import com.ninezero.data.remote.model.NetworkRequestInfo
import com.ninezero.data.remote.model.RequestType
import com.ninezero.data.remote.retrofit.NetworkRequestFactory
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val networkRequestFactory: NetworkRequestFactory
): RemoteDataSource {
    override suspend fun getHomeData(): ApiResult<HomeResponse> {
        return networkRequestFactory.create(
            url = "cream.json",
            requestInfo = NetworkRequestInfo.Builder(RequestType.GET).build(),
            type = HomeResponse::class.java
        )
    }
}