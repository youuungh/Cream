package com.ninezero.domain.repository

import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    val isNetworkAvailable: Flow<Boolean>
}