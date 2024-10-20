package com.ninezero.cream.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ninezero.cream.ui.auth.AuthState
import com.ninezero.domain.model.User
import com.ninezero.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState.UNAUTHENTICATED)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            val user = authUseCase.getCurrentUser()
            _authState.value = if (user != null) AuthState.AUTHENTICATED else AuthState.UNAUTHENTICATED
        }
    }

    fun setAuthState(state: AuthState) { _authState.value = state }
}