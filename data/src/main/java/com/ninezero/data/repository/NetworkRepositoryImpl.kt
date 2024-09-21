package com.ninezero.data.repository

import com.ninezero.data.datasource.NetworkStatus
import com.ninezero.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val networkStatus: NetworkStatus
) : NetworkRepository {
    override val isNetworkAvailable: Flow<Boolean> = networkStatus.isNetworkAvailable
}