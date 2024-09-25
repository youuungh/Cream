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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.ninezero.cream.utils.ANIMATION_DELAY
import com.ninezero.cream.utils.rememberSlideInOutAnimState
import com.ninezero.cream.viewmodel.ProductDetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    AnimatedContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onCartClick = onCartClick,
        onProductClick = onProductClick,
        onRefresh = { viewModel.action(ProductDetailAction.Refresh) },
        onSaveToggle = { viewModel.action(ProductDetailAction.ToggleSave) }
    )

    LaunchedEffect(networkState) {
        Timber.d("networkState: $networkState")
    }
}

@Composable
fun AnimatedContent(
    uiState: ProductDetailState,
    onNavigateBack: () -> Unit,
    onCartClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onRefresh: () -> Unit,
    onSaveToggle: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    var appBarAlpha by remember { mutableFloatStateOf(0f) }
    var appBarHeight by remember { mutableStateOf(0.dp) }
    var tabVisible by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    val animState = rememberSlideInOutAnimState()
    val coroutineScope = rememberCoroutineScope()
    val handleNavigateBack: () -> Unit = {
        coroutineScope.launch {
            visible = false
            delay(ANIMATION_DELAY.toLong())
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is ProductDetailState.Content) {
            visible = true
            delay(ANIMATION_DELAY.toLong())
            tabVisible = true
        } else {
            visible = false
            tabVisible = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = animState.enterTransition,
            exit = animState.exitTransition
        ) {
            Box {
                when (uiState) {
                    is ProductDetailState.Fetching -> ProductDetailSkeleton()
                    is ProductDetailState.Content -> ProductDetailContent(
                        state = uiState,
                        onSaveToggle = onSaveToggle,
                        onProductClick = onProductClick,
                        onBuyClick = { /*TODO*/ },
                        updateAppBarAlpha = { appBarAlpha = it },
                        appBarHeight = appBarHeight,
                        tabVisible = tabVisible,
                    )
                    is ProductDetailState.Error -> ErrorScreen(onRetry = onRefresh)
                }

                DetailsAppBar(
                    title = "",
                    onBackClick = handleNavigateBack,
                    onCartClick = onCartClick,
                    alpha = if (uiState is ProductDetailState.Content) appBarAlpha else 1f,
                    showCartButton = uiState is ProductDetailState.Content,
                    modifier = Modifier.onGloballyPositioned {
                        appBarHeight = with(density) { it.size.height.toDp() }
                    }
                )
            }
        }
    }
}