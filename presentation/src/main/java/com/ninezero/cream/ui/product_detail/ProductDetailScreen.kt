package com.ninezero.cream.ui.product_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ninezero.cream.base.collectAsState
import com.ninezero.cream.base.collectEvents
import com.ninezero.cream.ui.component.CustomSnackbar
import com.ninezero.cream.ui.component.DetailsAppBar
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.ProductDetailContent
import com.ninezero.cream.ui.component.bottomsheet.BottomSheetType
import com.ninezero.cream.ui.component.bottomsheet.DetailBottomSheetState
import com.ninezero.cream.ui.component.rememberCreamScaffoldState
import com.ninezero.cream.ui.component.skeleton.ProductDetailSkeleton
import com.ninezero.cream.utils.BOTTOM_BAR_HEIGHT
import com.ninezero.cream.utils.rememberSlideInOutAnimState
import com.ninezero.cream.viewmodel.ProductDetailViewModel
import timber.log.Timber

@Composable
fun ProductDetailScreen(
    onCartClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSaved: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val creamScaffoldState = rememberCreamScaffoldState()

    var visible by remember { mutableStateOf(false) }
    var appBarAlpha by remember { mutableFloatStateOf(0f) }
    var appBarHeight by remember { mutableStateOf(0.dp) }
    var tabVisible by remember { mutableStateOf(false) }
    var bottomSheetState by remember { mutableStateOf(DetailBottomSheetState()) }

    val animState = rememberSlideInOutAnimState()

    viewModel.collectEvents {
        when (it) {
            is ProductDetailEvent.NavigateToLogin -> onNavigateToLogin()
            is ProductDetailEvent.NavigateToSaved -> onNavigateToSaved()
            is ProductDetailEvent.NavigateToCart -> onCartClick()
            is ProductDetailEvent.ShowSnackbar -> creamScaffoldState.showSnackbar(it.message)
        }
    }

    LaunchedEffect(Unit) { visible = true }

    LaunchedEffect(uiState) {
        if (uiState is ProductDetailState.Content) {
            tabVisible = true
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = animState.enterTransition,
        exit = animState.exitTransition
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is ProductDetailState.Fetching -> ProductDetailSkeleton()

                is ProductDetailState.Content -> ProductDetailContent(
                    state = state,
                    onProductClick = onProductClick,
                    onSaveToggle = { product ->
                        viewModel.action(ProductDetailAction.ToggleSave(product))
                    },
                    onAddToCart = {
                        viewModel.action(ProductDetailAction.AddToCart(state.product))
                        bottomSheetState = DetailBottomSheetState()
                    },
                    onBuyNow = { bottomSheetState = bottomSheetState.copy(type = BottomSheetType.Payment) },
                    updateAppBarAlpha = { appBarAlpha = it },
                    appBarHeight = appBarHeight,
                    tabVisible = tabVisible,
                    bottomSheetState = bottomSheetState,
                    onBottomSheetStateChange = { newState -> bottomSheetState = newState }
                )

                is ProductDetailState.Error -> ErrorScreen(
                    onRetry = { viewModel.action(ProductDetailAction.Fetch) }
                )
            }

            ProductDetailAppBar(
                onBackClick = {
                    visible = false
                    onNavigateBack()
                },
                onCartClick = onCartClick,
                appBarAlpha = appBarAlpha,
                showCartButton = uiState is ProductDetailState.Content,
                onHeightChanged = { appBarHeight = it }
            )

            SnackbarHost(
                hostState = creamScaffoldState.snackBarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = BOTTOM_BAR_HEIGHT.dp)
                    .navigationBarsPadding(),
                snackbar = { snackbarData -> CustomSnackbar(snackbarData = snackbarData) }
            )
        }
    }
}

@Composable
private fun ProductDetailAppBar(
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    appBarAlpha: Float,
    showCartButton: Boolean,
    onHeightChanged: (height: androidx.compose.ui.unit.Dp) -> Unit
) {
    val density = LocalDensity.current
    DetailsAppBar(
        title = "",
        onBackClick = onBackClick,
        onCartClick = onCartClick,
        alpha = appBarAlpha,
        showCartButton = showCartButton,
        modifier = Modifier.onGloballyPositioned { coordinates ->
            onHeightChanged(with(density) { coordinates.size.height.toDp() })
        }
    )
}