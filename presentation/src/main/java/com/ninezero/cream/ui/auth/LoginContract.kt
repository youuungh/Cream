package com.ninezero.cream.ui.auth

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.cream.model.Message
import com.ninezero.domain.model.User
import javax.inject.Inject

sealed class LoginAction : MviAction {
    data class SignInWithGoogle(val idToken: String) : LoginAction()
    data class SignInWithNaver(val accessToken: String) : LoginAction()
    data class SignInWithKakao(val accessToken: String) : LoginAction()
    data class LoginError(val message: String) : LoginAction()
}

sealed class LoginResult : MviResult {
    object Loading : LoginResult()
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

sealed class LoginEvent : MviEvent {
    data class ShowSnackbar(val message: Message) : LoginEvent()
}

sealed class LoginState : MviViewState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class LoggedIn(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginReducer @Inject constructor() : MviStateReducer<LoginState, LoginResult> {
    override fun LoginState.reduce(result: LoginResult): LoginState {
        return when (result) {
            is LoginResult.Loading -> LoginState.Loading
            is LoginResult.Success -> LoginState.LoggedIn(result.user)
            is LoginResult.Error -> LoginState.Error(result.message)
        }
    }
}