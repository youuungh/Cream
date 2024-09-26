package com.ninezero.cream.ui.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ninezero.cream.base.collectAsState
import com.ninezero.cream.ui.component.DetailsAppBar
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.ProductDetailContent
import com.ninezero.cream.ui.component.skeleton.ProductDetailSkeleton
import com.ninezero.cream.utils.rememberSlideInOutAnimState
import com.ninezero.cream.viewmodel.ProductDetailViewModel
import timber.log.Timber

@Composable
fun ProductDetailScreen(
    onNavigateBack: () -> Unit,
    onCartClick: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val networkState by viewModel.networkState.collectAsState()

    var visible by remember { mutableStateOf(false) }
    var appBarAlpha by remember { mutableFloatStateOf(0f) }
    var appBarHeight by remember { mutableStateOf(0.dp) }
    var tabVisible by remember { mutableStateOf(false) }

    val animState = rememberSlideInOutAnimState()

    LaunchedEffect(Unit) { visible = true }

    LaunchedEffect(uiState) {
        if (uiState is ProductDetailState.Content) {
            tabVisible = true
        }
    }

    LaunchedEffect(networkState) {
        Timber.d("networkState: $networkState")
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
                    onSaveToggle = { product ->
                        viewModel.action(
                            ProductDetailAction.ToggleSave(
                                product
                            )
                        )
                    },
                    onProductClick = onProductClick,
                    onBuyClick = { /* TODO */ },
                    updateAppBarAlpha = { appBarAlpha = it },
                    appBarHeight = appBarHeight,
                    tabVisible = tabVisible
                )

                is ProductDetailState.Error -> ErrorScreen(
                    onRetry = { viewModel.action(ProductDetailAction.Refresh) }
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