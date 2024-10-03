package com.ninezero.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface NetworkRepository {
    val isNetworkAvailable: StateFlow<Boolean>
}