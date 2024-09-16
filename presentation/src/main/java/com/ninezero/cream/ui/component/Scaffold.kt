package com.ninezero.cream.ui.component

import android.content.res.Resources
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.ninezero.cream.utils.SnackbarUtils
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
        content = content
    )
}

@Composable
fun rememberCreamScaffoldState(
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarUtils: SnackbarUtils = SnackbarUtils,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) : CreamScaffoldState  = remember(snackBarHostState, snackbarUtils, resources, coroutineScope) {
    CreamScaffoldState(snackBarHostState, snackbarUtils, resources, coroutineScope)
}

@Stable
class CreamScaffoldState(
    val snackBarHostState: SnackbarHostState,
    private val snackbarUtils: SnackbarUtils,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {
    init {
        coroutineScope.launch {
            snackbarUtils.messages.collect { messages ->
                messages.firstOrNull()?.let {
                    val text = resources.getText(it.messageId)
                    snackbarUtils.setMessageShown(it.id)
                    snackBarHostState.showSnackbar(text.toString())
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}