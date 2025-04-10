@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
package com.ninezero.cream.ui.category_detail

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ninezero.cream.base.collectAsState
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.ProductCard
import com.ninezero.cream.ui.component.VerticalGridDetail
import com.ninezero.cream.ui.component.skeleton.CategoryDetailSkeleton
import com.ninezero.cream.base.collectEvents
import com.ninezero.cream.ui.LocalNavAnimatedVisibilityScope
import com.ninezero.cream.ui.LocalSharedTransitionScope
import com.ninezero.cream.ui.component.CreamScaffold
import com.ninezero.cream.ui.component.CustomSnackbar
import com.ninezero.cream.ui.component.rememberCreamScaffoldState
import com.ninezero.cream.utils.CategorySharedElementKey
import com.ninezero.cream.utils.CategorySharedElementType
import com.ninezero.cream.utils.detailBoundsTransform
import com.ninezero.cream.utils.nonSpatialExpressiveSpring
import com.ninezero.cream.viewmodel.CategoryDetailViewModel

@Composable
fun CategoryDetailScreen(
    categoryId: String,
    categoryName: String,
    onProductClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSaved: () -> Unit,
    viewModel: CategoryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val scaffoldState = rememberCreamScaffoldState()

    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No SharedTransitionScope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No AnimatedVisibilityScope found")

    val roundedCornerAnimation by animatedVisibilityScope.transition
        .animateDp(
            label = "rounded_corner",
            transitionSpec = { tween(durationMillis = 300, easing = FastOutSlowInEasing) }
        ) { enterExit: EnterExitState ->
            when (enterExit) {
                EnterExitState.PreEnter -> 16.dp
                EnterExitState.Visible -> 0.dp
                EnterExitState.PostExit -> 16.dp
            }
        }

    viewModel.collectEvents {
        when (it) {
            is CategoryDetailEvent.NavigateToProductDetail -> onProductClick(it.productId)
            is CategoryDetailEvent.NavigateToLogin -> onNavigateToLogin()
            is CategoryDetailEvent.NavigateToSaved -> onNavigateToSaved()
            is CategoryDetailEvent.ShowSnackbar -> scaffoldState.showSnackbar(it.message)
        }
    }

    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    clip = true
                    shape = RoundedCornerShape(roundedCornerAnimation)
                }
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(
                        key = CategorySharedElementKey(
                            categoryId = categoryId,
                            categoryName = categoryName,
                            type = CategorySharedElementType.Bounds
                        )
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = detailBoundsTransform,
                    clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(roundedCornerAnimation)),
                    enter = fadeIn(nonSpatialExpressiveSpring()),
                    exit = fadeOut(nonSpatialExpressiveSpring())
                )
        ) {
            CreamScaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = categoryName) },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Back"
                                )
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
                snackbarHostState = scaffoldState.snackBarHostState,
            ) { innerPadding ->
                when (val state = uiState) {
                    is CategoryDetailState.Fetching -> CategoryDetailSkeleton(
                        modifier = Modifier.padding(innerPadding)
                    )

                    is CategoryDetailState.Content -> {
                        key(state.categoryDetails.category.categoryId) {
                            VerticalGridDetail(
                                items = state.categoryDetails.products,
                                columns = 2,
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                key(it.productId) {
                                    ProductCard(
                                        product = it,
                                        onClick = { onProductClick(it.productId) },
                                        onSaveToggle = { viewModel.action(CategoryDetailAction.ToggleSave(it)) }
                                    )
                                }
                            }
                        }
                    }

                    is CategoryDetailState.Error -> ErrorScreen(
                        onRetry = { viewModel.action(CategoryDetailAction.Fetch) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}