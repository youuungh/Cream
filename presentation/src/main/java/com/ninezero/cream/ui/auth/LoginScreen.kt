@file:OptIn(ExperimentalMaterial3Api::class)

package com.ninezero.cream.ui.auth

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.ninezero.cream.base.collectEvents
import com.ninezero.cream.model.Message
import com.ninezero.cream.ui.component.CreamScaffold
import com.ninezero.cream.ui.component.CustomSnackbar
import com.ninezero.cream.ui.component.SocialLoginButtons
import com.ninezero.cream.ui.component.rememberCreamScaffoldState
import com.ninezero.cream.ui.theme.logoSubtitle
import com.ninezero.cream.ui.theme.logoTitle
import com.ninezero.cream.viewmodel.LoginViewModel
import com.ninezero.di.R
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val creamScaffoldState = rememberCreamScaffoldState()

    val googleSignInLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                viewModel.handleGoogleSignInResult(task)
            }
        }

    val naverLoginCallback = remember {
        object : OAuthLoginCallback {
            override fun onSuccess() {
                NaverIdLoginSDK.getAccessToken()?.let { token ->
                    viewModel.action(LoginAction.SignInWithNaver(token))
                } ?: run {
                    viewModel.action(LoginAction.LoginError("Naver login succeeded but access token is null"))
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                viewModel.action(LoginAction.LoginError("Naver login failed: $errorCode, $errorDescription"))
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }
    }

    viewModel.collectEvents {
        when (it) {
            is LoginEvent.ShowSnackbar -> creamScaffoldState.showSnackbar(it.message)
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is LoginState.LoggedIn -> onLoginSuccess()
            is LoginState.Error -> {
                val errorMessage =
                    context.getString(R.string.login_error, (state as LoginState.Error).message)
                creamScaffoldState.showSnackbar(
                    Message(
                        messageId = R.string.login_error,
                        message = errorMessage
                    )
                )
            }
            else -> {}
        }
    }

    CreamScaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = it,
                modifier = Modifier.navigationBarsPadding(),
                snackbar = { snackbarData -> CustomSnackbar(snackbarData) }
            )
        },
        snackbarHostState = creamScaffoldState.snackBarHostState
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.logo_title),
                        style = logoTitle,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.logo_subtitle),
                        style = logoSubtitle,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                SocialLoginButtons(
                    onGoogleLogin = { googleSignInLauncher.launch(viewModel.getGoogleSignInIntent()) },
                    onNaverLogin = { NaverIdLoginSDK.authenticate(context, naverLoginCallback) },
                    onKakaoLogin = {
                        scope.launch {
                            handleKakaoLogin(
                                context,
                                onSuccess = { token ->
                                    viewModel.action(
                                        LoginAction.SignInWithKakao(
                                            token.accessToken
                                        )
                                    )
                                },
                                onError = { error -> viewModel.action(LoginAction.LoginError("Kakao login failed: ${error.message}")) },
                                onCancel = { viewModel.action(LoginAction.LoginError("Kakao login cancelled")) }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(48.dp))
            }

            if (state is LoginState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

private fun handleKakaoLogin(
    context: Context,
    onSuccess: (OAuthToken) -> Unit,
    onError: (Throwable) -> Unit,
    onCancel: () -> Unit
) {
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        when {
            error != null -> {
                Timber.tag("KakaoLogin").e(error, "카카오계정으로 로그인 실패")
                onError(error)
            }

            token != null -> {
                Timber.tag("KakaoLogin").i("카카오계정으로 로그인 성공 %s", token.accessToken)
                onSuccess(token)
            }
        }
    }

    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            when {
                error != null -> {
                    Timber.tag("KakaoLogin").e(error, "카카오톡으로 로그인 실패")
                    if (error !is ClientError || error.reason != ClientErrorCause.Cancelled) {
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    } else {
                        onCancel()
                    }
                }

                token != null -> {
                    Timber.tag("KakaoLogin").i("카카오톡으로 로그인 성공 %s", token.accessToken)
                    onSuccess(token)
                }
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}