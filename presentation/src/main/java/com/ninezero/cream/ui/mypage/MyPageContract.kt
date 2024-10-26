package com.ninezero.cream.ui.mypage

import com.ninezero.cream.base.MviAction
import com.ninezero.cream.base.MviEvent
import com.ninezero.cream.base.MviResult
import com.ninezero.cream.base.MviStateReducer
import com.ninezero.cream.base.MviViewState
import com.ninezero.domain.model.Order
import com.ninezero.domain.model.User
import javax.inject.Inject

sealed class MyPageAction : MviAction {
    object Fetch : MyPageAction()
    object NavigateToHome : MyPageAction()
    data class UpdateOrders(val orders: List<Order>) : MyPageAction()
    object SignOut : MyPageAction()
    data class Error(val message: String) : MyPageAction()
}

sealed class MyPageResult : MviResult {
    object Fetching : MyPageResult()
    data class ContentLoaded(val user: User?, val orders: List<Order>) : MyPageResult()
    data class OrdersFetched(val orders: List<Order>) : MyPageResult()
    object SignedOut : MyPageResult()
    data class Error(val message: String) : MyPageResult()
}

sealed class MyPageEvent : MviEvent, MyPageResult() {
    object NavigateToHome : MyPageEvent()
}

sealed class MyPageState : MviViewState {
    object Fetching : MyPageState()
    data class Content(
        val user: User?,
        val orders: List<Order> = emptyList(),
        val isOrdersLoaded: Boolean = false
    ) : MyPageState()
    data class Error(val message: String) : MyPageState()
}

class MyPageReducer @Inject constructor() : MviStateReducer<MyPageState, MyPageResult> {
    override fun MyPageState.reduce(result: MyPageResult): MyPageState = when (result) {
        is MyPageResult.Fetching -> MyPageState.Fetching
        is MyPageResult.ContentLoaded -> MyPageState.Content(
            user = result.user,
            orders = result.orders,
            isOrdersLoaded = false
        )
        is MyPageResult.OrdersFetched -> when (this) {
            is MyPageState.Content -> copy(orders = result.orders, isOrdersLoaded = true)
            else -> MyPageState.Content(user = null, orders = result.orders, isOrdersLoaded = true)
        }
        is MyPageResult.SignedOut -> MyPageState.Content(user = null)
        is MyPageResult.Error -> MyPageState.Error(result.message)
        is MyPageEvent -> this
    }
}