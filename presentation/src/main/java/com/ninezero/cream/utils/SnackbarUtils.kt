package com.ninezero.cream.utils

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import com.ninezero.cream.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

object SnackbarUtils {
    private val _messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages.asStateFlow()

    fun showMessage(@StringRes messageTextId: Int, duration: SnackbarDuration = SnackbarDuration.Short) {
        _messages.update { currentMessages ->
            currentMessages + Message(messageId = messageTextId, duration = duration)
        }
    }

    fun setMessageShown(messageId: Long) {
        _messages.update { currentMessages -> currentMessages.filterNot { it.id == messageId } }
    }
}