package com.ninezero.cream.model

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import java.util.UUID

data class Message(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    @StringRes
    val messageId: Int,
    val duration: SnackbarDuration = SnackbarDuration.Short
)