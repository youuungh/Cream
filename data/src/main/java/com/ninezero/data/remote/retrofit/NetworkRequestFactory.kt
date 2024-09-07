package com.ninezero.data.remote.retrofit

import com.ninezero.data.remote.model.ApiResult
import com.ninezero.data.remote.model.NetworkRequestInfo
import java.lang.reflect.Type

interface NetworkRequestFactory {
    suspend fun <T> create(
        url: String,
        requestInfo: NetworkRequestInfo = NetworkRequestInfo.Builder().build(),
        type: Type
    ): ApiResult<T>
}