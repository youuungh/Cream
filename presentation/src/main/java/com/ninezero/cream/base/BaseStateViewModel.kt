package com.ninezero.cream.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ninezero.cream.utils.NETWORK_DELAY
import com.ninezero.cream.utils.NO_INTERNET_CONNECTION
import com.ninezero.domain.repository.NetworkRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

abstract class BaseStateViewModel<Action : MviAction, Result : MviResult, Event : MviEvent, State : MviViewState, Reducer : MviStateReducer<State, Result>>(
    initialState: State,
    private val reducer: Reducer,
    private val networkRepository: NetworkRepository
) : ViewModel(), MviStateReducer<State, Result> by reducer {

    private val _fsmFlow = MutableSharedFlow<Action>(
        extraBufferCapacity = 20,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val _event = MutableSharedFlow<Event>(
        extraBufferCapacity = 20,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<Event> = _event

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state

    val networkState: StateFlow<Boolean> = networkRepository.isNetworkAvailable

    init {
        setupStateMachine()
        observeNetworkStatus()
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkState.collect { isAvailable ->
                if (isAvailable && shouldRefreshOnConnect()) refreshData()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Suppress("UNCHECKED_CAST")
    private fun setupStateMachine() {
        _fsmFlow
            .flatMapConcat { it.process() }
            .scan(_state.value) { previousState, result ->
                if (result is MviEvent) {
                    _event.tryEmit(result as Event)
                    previousState
                } else {
                    previousState reduce result
                }
            }
            .onEach {
                _state.value = it
            }
            .launchIn(viewModelScope)
    }

    protected abstract fun Action.process(): Flow<Result>

    protected fun emitResult(result: Result) = flow { emit(result) }

    protected fun emitEvent(event: Event) = flow { emit(event) }

    fun action(action: Action) {
        _fsmFlow.tryEmit(action)
    }

    protected open fun shouldRefreshOnConnect(): Boolean = false

    protected suspend fun <T> handleNetworkCallback(call: suspend () -> Flow<T>): Flow<T> = flow {
        if (!networkState.value) {
            kotlinx.coroutines.delay(NETWORK_DELAY)
            throw Exception(NO_INTERNET_CONNECTION)
        } else emitAll(call())
    }

    abstract fun refreshData()
}
