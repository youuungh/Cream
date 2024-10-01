package com.ninezero.cream.utils

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import com.ninezero.cream.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object SnackbarUtils {
    private val _messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages.asStateFlow()

    fun showSnack(
        @StringRes messageTextId: Int,
        duration: SnackbarDuration = SnackbarDuration.Short,
        @StringRes actionLabelId: Int? = null,
        onAction: (() -> Unit)? = null
    ) {
        _messages.update { currentMessages ->
            val newMessage = Message(
                messageId = messageTextId,
                duration = duration,
                actionLabelId = actionLabelId,
                onAction = onAction
            )

            if (currentMessages.none { it.messageId == messageTextId }) {
                currentMessages + newMessage
            } else {
                currentMessages
            }
        }
    }

    fun setSnackShown(messageId: Long) {
        _messages.update { currentMessages -> currentMessages.filterNot { it.id == messageId }.take(3) }
    }
}