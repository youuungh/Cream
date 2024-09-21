package com.ninezero.cream.ui.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val networkState by viewModel.networkState.collectAsState()
    val (visible, setVisible) = remember { mutableStateOf(false) }
    val animState = rememberSlideInOutAnimState()
    val coroutineScope = rememberCoroutineScope()

    var appBarAlpha by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(uiState) { if (uiState is ProductDetailState.Content) setVisible(true) }

    LaunchedEffect(networkState) {
        Timber.d("networkState: $networkState")
    }

    val handleNavigateBack: () -> Unit = {
        coroutineScope.launch {
            setVisible(false)
            delay(ANIMATION_DELAY.toLong())
            onNavigateBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = uiState) {
            is ProductDetailState.Loading -> {
                AnimatedVisibility(
                    visible = !visible,
                    enter = animState.enterTransition,
                    exit = animState.exitTransition
                ) {
                    ProductDetailSkeleton()
                }
            }

            is ProductDetailState.Content -> {
                AnimatedVisibility(
                    visible = visible,
                    enter = animState.enterTransition,
                    exit = animState.exitTransition
                ) {
                    ProductDetailContent(
                        state = state,
                        onSaveToggle = { viewModel.action(ProductDetailAction.ToggleSave) },
                        onBuyClick = { /*TODO*/ },
                        updateAppBarAlpha = { appBarAlpha = it }
                    )
                }
            }

            is ProductDetailState.Error -> {
                AnimatedVisibility(
                    visible = !visible,
                    enter = animState.enterTransition,
                    exit = animState.exitTransition
                ) {
                    ErrorScreen(
                        onRetry = { viewModel.action(ProductDetailAction.Refresh) }
                    )
                }
            }
        }

        DetailsAppBar(
            title = "",
            onBackClick = handleNavigateBack,
            onCartClick = onCartClick,
            alpha = appBarAlpha,
            showCartButton = when (uiState) {
                is ProductDetailState.Content -> true
                else -> false
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}