package com.ninezero.cream.viewmodel

import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.model.Message
import com.ninezero.cream.ui.auth.LoginAction
import com.ninezero.cream.ui.auth.LoginEvent
import com.ninezero.cream.ui.auth.LoginReducer
import com.ninezero.cream.ui.auth.LoginResult
import com.ninezero.cream.ui.auth.LoginState
import com.ninezero.cream.utils.ErrorHandler
import com.ninezero.di.R
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val googleSignInClient: GoogleSignInClient,
    reducer: LoginReducer,
    networkRepository: NetworkRepository
) : BaseStateViewModel<LoginAction, LoginResult, LoginEvent, LoginState, LoginReducer>(
    initialState = LoginState.Idle,
    reducer = reducer,
    networkRepository = networkRepository
) {

    override fun LoginAction.process(): Flow<LoginResult> = flow {
        emit(LoginResult.Loading)
        try {
            val result = when (this@process) {
                is LoginAction.SignInWithGoogle -> authUseCase.signInWithGoogle(idToken)
                is LoginAction.SignInWithNaver -> authUseCase.signInWithNaver(accessToken)
                is LoginAction.SignInWithKakao -> authUseCase.signInWithKakao(accessToken)
                is LoginAction.LoginError -> Result.failure(Exception(message))
            }

            handleNetworkCallback { flowOf(result) }
                .map { loginResult ->
                    loginResult.fold(
                        onSuccess = { LoginResult.Success(it) },
                        onFailure = { LoginResult.Error(ErrorHandler.getErrorMessage(it)) }
                    )
                }
                .collect { emit(it) }
        } catch (e: Exception) {
            emit(LoginResult.Error(ErrorHandler.getErrorMessage(e)))
            emitEvent(LoginEvent.ShowSnackbar(Message(messageId = R.string.network_error)))
        }
    }

    fun getGoogleSignInIntent() = googleSignInClient.signInIntent

    fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            try {
                val account = completedTask.getResult(ApiException::class.java)
                account?.idToken?.let { token ->
                    action(LoginAction.SignInWithGoogle(token))
                } ?: action(LoginAction.LoginError("Google ID token is null"))
            } catch (e: ApiException) {
                action(LoginAction.LoginError("Google sign in failed: ${e.statusCode}"))
            }
        }
    }

    override fun refreshData() { }
}