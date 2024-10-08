package com.ninezero.cream.ui.component

import android.content.res.Resources
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ninezero.cream.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CreamScaffold(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    topBar: @Composable (() -> Unit) = {},
    bottomBar: @Composable (() -> Unit) = {},
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(hostState = it) },
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = { snackbarHost(snackbarHostState) },
        content = content,
        contentWindowInsets = WindowInsets(0.dp)
    )
}

@Composable
fun rememberCreamScaffoldState(
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) : CreamScaffoldState  = remember(snackBarHostState, resources, coroutineScope) {
    CreamScaffoldState(snackBarHostState, resources, coroutineScope)
}

@Stable
class CreamScaffoldState(
    val snackBarHostState: SnackbarHostState,
    private val resources: Resources,
    private val coroutineScope: CoroutineScope
) {
    fun showSnackbar(message: Message) {
        coroutineScope.launch {
            val text = resources.getText(message.messageId)
            val actionLabel = message.actionLabelId?.let { resources.getText(it) }

            val result = snackBarHostState.showSnackbar(
                message = text.toString(),
                actionLabel = actionLabel?.toString(),
                duration = message.duration
            )
            if (result == SnackbarResult.ActionPerformed)
                message.onAction?.invoke()
        }
    }
}

@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}