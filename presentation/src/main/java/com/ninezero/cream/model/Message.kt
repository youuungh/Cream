package com.ninezero.cream.model

import androidx.compose.material3.SnackbarDuration
import java.util.UUID

data class Message(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val messageId: Int,
    val message: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val actionLabelId: Int? = null,
    val onAction: (() -> Unit)? = null
)