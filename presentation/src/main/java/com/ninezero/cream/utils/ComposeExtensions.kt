package com.ninezero.cream.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <A : MviAction, R : MviResult, E : MviEvent, S : MviViewState, RED : MviStateReducer<S, R>>
        BaseStateViewModel<A, R, E, S, RED>.collectEvents(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    eventHandler: suspend (event: E) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(this, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            event.collect { eventHandler(it) }
        }
    }
}

@Composable
fun <T> StateFlow<T>.collectAsState(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED
): State<T> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val initialValue = remember { value }

    return produceState(initialValue, this, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            collect { value = it }
        }
    }
}