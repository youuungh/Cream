package com.ninezero.data.repository

import com.ninezero.data.datasource.NetworkStatus
import com.ninezero.domain.repository.NetworkRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val networkStatus: NetworkStatus
) : NetworkRepository {
    override val isNetworkAvailable = MutableStateFlow(true)

    init {
        observeNetworkStatus()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun observeNetworkStatus() {
        networkStatus.isNetworkAvailable
            .onEach { isAvailable -> isNetworkAvailable.value = isAvailable }
            .launchIn(GlobalScope)
    }
}