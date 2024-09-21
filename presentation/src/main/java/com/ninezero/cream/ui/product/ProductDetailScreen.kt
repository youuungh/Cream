package com.ninezero.cream.ui.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun ProductDetailScreen(
    onNavigateBack: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val (visible, setVisible) = remember { mutableStateOf(false) }
    val animState = rememberSlideInOutAnimState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState) { if (uiState is ProductDetailState.Content) setVisible(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = animState.enterTransition,
            exit = animState.exitTransition
        ) {
            when (val state = uiState) {
                is ProductDetailState.Loading -> ProductDetailSkeleton()

                is ProductDetailState.Content -> {
                    ProductDetailContent(
                        state = state,
                        onNavigateBack = {
                            coroutineScope.launch {
                                setVisible(false)
                                delay(ANIMATION_DELAY.toLong())
                                onNavigateBack()
                            }
                        },
                        onCartClick = onCartClick,
                        onSaveToggle = { viewModel.action(ProductDetailAction.ToggleSave) },
                        onBuyClick = { /*TODO*/ }
                    )
                }

                is ProductDetailState.Error -> ErrorScreen(
                    onRetry = { viewModel.action(ProductDetailAction.Refresh) }
                )
            }
        }
    }
}