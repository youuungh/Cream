package com.ninezero.cream.ui.mypage

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ninezero.cream.base.collectEvents
import com.ninezero.cream.ui.component.AdMobBanner
import com.ninezero.cream.ui.component.CreamPullRefresh
import com.ninezero.cream.ui.component.CreamScaffold
import com.ninezero.cream.ui.component.CreamSurface
import com.ninezero.cream.ui.component.CreamTopAppBar
import com.ninezero.cream.ui.component.CustomDialog
import com.ninezero.cream.ui.component.DevInfo
import com.ninezero.cream.ui.component.EmptyScreen
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.LoadingOverlay
import com.ninezero.cream.ui.component.OrderProductCard
import com.ninezero.cream.ui.component.SectionTitle
import com.ninezero.cream.ui.component.UserProfileCard
import com.ninezero.cream.ui.component.skeleton.MyPageSkeleton
import com.ninezero.cream.utils.TEST_ADMOB_ID
import com.ninezero.cream.viewmodel.MyPageViewModel
import com.ninezero.di.R

@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
    onCartClick: () -> Unit,
    onSignOut: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: MyPageViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    var showSignOutDialog by remember { mutableStateOf(false) }
    var isSigningOut by remember { mutableStateOf(false) }
    val isRefresh by viewModel.isRefresh.collectAsState()

    viewModel.collectEvents {
        when (it) {
            MyPageEvent.NavigateToHome -> {
                onSignOut()
                onNavigateToHome()
            }
        }
    }

    CreamSurface(modifier = Modifier.fillMaxSize()) {
        CreamScaffold(
            topBar = {
                CreamTopAppBar(
                    title = stringResource(R.string.main_my_page),
                    onCartClick = onCartClick
                )
            }
        ) { innerPadding ->
            CreamPullRefresh(
                refreshing = isRefresh,
                onRefresh = { viewModel.refreshData() }
            ) {
                when (val state = uiState) {
                    is MyPageState.Fetching -> MyPageSkeleton(modifier = modifier.padding(innerPadding))
                    is MyPageState.Content -> {
                        Crossfade(
                            targetState = isSigningOut,
                            label = "sign_out"
                        ) {
                            if (it) {
                                LoadingOverlay(
                                    text = stringResource(R.string.signing_out),
                                    modifier = modifier.padding(innerPadding)
                                )
                            } else {
                                MyPageContent(
                                    state = state,
                                    modifier = modifier.padding(innerPadding),
                                    onSignOut = { showSignOutDialog = true }
                                )
                            }
                        }
                    }
                    is MyPageState.Error -> ErrorScreen(
                        onRetry = { viewModel.action(MyPageAction.Fetch) }
                    )
                }
            }
        }
    }

    if (showSignOutDialog) {
        CustomDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = stringResource(R.string.dialog_sign_out),
            content = stringResource(R.string.dialog_sign_out_confirm),
            confirmButtonText = stringResource(R.string.confirm),
            onConfirmClick = {
                isSigningOut = true
                viewModel.action(MyPageAction.SignOut)
                showSignOutDialog = false
            }
        )
    }
}

@Composable
fun MyPageContent(
    state: MyPageState.Content,
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            UserProfileCard(
                user = state.user,
                onSignOut = onSignOut,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            SectionTitle(
                title = stringResource(R.string.order_details),
                subtitle = stringResource(R.string.order_details_subtitle),
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            )
        }

        if (!state.isOrdersLoaded) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(0.6f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (state.orders.isEmpty()) {
            item {
                EmptyScreen(
                    title = stringResource(R.string.no_order_details),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(0.6f)
                        .padding(vertical = 32.dp)
                )
            }
        } else {
            items(state.orders) {
                OrderProductCard(
                    order = it,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        item { DevInfo() }
        item { AdMobBanner(adMobId = TEST_ADMOB_ID) }
    }
}