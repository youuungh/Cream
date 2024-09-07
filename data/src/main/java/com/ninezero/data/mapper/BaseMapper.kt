package com.ninezero.data.mapper

import com.ninezero.domain.model.EntityWrapper
import com.ninezero.data.remote.model.ApiResponse
import com.ninezero.data.remote.model.ApiResult

abstract class BaseMapper<M, E> {
    fun mapFromResult(result: ApiResult<M>, extra: Any? = null): EntityWrapper<E> =
        when (result.response) {
            is ApiResponse.Success -> getSuccess(model = result.response.data, extra = extra)
            is ApiResponse.Fail -> getFailure(error = result.response.error)
        }

    abstract fun getSuccess(model: M?, extra: Any?): EntityWrapper.Success<E>
    abstract fun getFailure(error: Throwable): EntityWrapper.Fail<E>
}