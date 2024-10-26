package com.ninezero.cream.viewmodel

import androidx.lifecycle.viewModelScope
import com.ninezero.cream.base.BaseStateViewModel
import com.ninezero.cream.ui.mypage.MyPageAction
import com.ninezero.cream.ui.mypage.MyPageEvent
import com.ninezero.cream.ui.mypage.MyPageReducer
import com.ninezero.cream.ui.mypage.MyPageResult
import com.ninezero.cream.ui.mypage.MyPageState
import com.ninezero.cream.utils.ErrorHandler
import com.ninezero.domain.repository.NetworkRepository
import com.ninezero.domain.usecase.AuthUseCase
import com.ninezero.domain.usecase.CartUseCase
import com.ninezero.domain.usecase.OrderUseCase
import com.ninezero.domain.usecase.SaveUseCase
import com.ninezero.domain.usecase.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val orderUseCase: OrderUseCase,
    private val cartUseCase: CartUseCase,
    private val saveUseCase: SaveUseCase,
    private val searchUseCase: SearchUseCase,
    reducer: MyPageReducer,
    networkRepository: NetworkRepository
) : BaseStateViewModel<MyPageAction, MyPageResult, MyPageEvent, MyPageState, MyPageReducer>(
    initialState = MyPageState.Fetching,
    reducer = reducer,
    networkRepository = networkRepository
) {

    init {
        action(MyPageAction.Fetch)
        observeOrders()
    }

    override fun MyPageAction.process(): Flow<MyPageResult> = when (this@process) {
        is MyPageAction.Fetch -> fetchUserAndOrders()
        is MyPageAction.SignOut -> signOut()
        is MyPageAction.UpdateOrders -> emitResult(MyPageResult.OrdersFetched(orders))
        is MyPageAction.NavigateToHome -> emitEvent(MyPageEvent.NavigateToHome)
        is MyPageAction.Error -> emitResult(MyPageResult.Error(message))
    }

    private fun fetchUserAndOrders(): Flow<MyPageResult> = flow {
        emit(MyPageResult.Fetching)
        try {
            handleNetworkCallback {
                flow {
                    val user = authUseCase.getCurrentUser()
                    emit(user)
                }
            }.collect { user ->
                emit(MyPageResult.ContentLoaded(user, emptyList()))
            }
        } catch (e: Exception) {
            emit(MyPageResult.Error(ErrorHandler.getErrorMessage(e)))
        }
    }

    private fun observeOrders() {
        viewModelScope.launch {
            try {
                handleNetworkCallback { orderUseCase.getOrders() }
                    .collect { orders ->
                        action(MyPageAction.UpdateOrders(orders))
                    }
            } catch (e: Exception) {
                action(MyPageAction.Error(ErrorHandler.getErrorMessage(e)))
            }
        }
    }

    private suspend fun clearUserData() {
        try {
            cartUseCase.removeAll()
            saveUseCase.removeAll()
            searchUseCase.clearSearchHistory()
        } catch (e: Exception) {
            Timber.e("Error clearing user data: ${e.message}")
        }
    }

    private fun signOut(): Flow<MyPageResult> = flow {
        try {
            clearUserData()
            authUseCase.signOut()
                .onSuccess {
                    emit(MyPageResult.SignedOut)
                    action(MyPageAction.NavigateToHome)
                }
                .onFailure {
                    emit(MyPageResult.Error(it.message ?: "Unknown error"))
                }
        } catch (e: Exception) {
            emit(MyPageResult.Error(ErrorHandler.getErrorMessage(e)))
        }
    }

    override fun shouldRefreshOnConnect(): Boolean = state.value is MyPageState.Error
    override fun refreshData() {
        action(MyPageAction.Fetch)
        observeOrders()
    }
}