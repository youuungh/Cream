@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ninezero.cream.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.ninezero.cream.ui.category.CategoryDetailScreen
import com.ninezero.cream.ui.component.BottomBar
import com.ninezero.cream.ui.component.CreamScaffold
import com.ninezero.cream.ui.component.CustomSnackbar
import com.ninezero.cream.ui.component.rememberCreamScaffoldState
import com.ninezero.cream.ui.navigation.AppRoutes
import com.ninezero.cream.ui.navigation.addMainGraph
import com.ninezero.cream.ui.navigation.composableWithCompositionLocal
import com.ninezero.cream.ui.navigation.rememberCreamNavController
import com.ninezero.cream.ui.theme.CreamTheme
import com.ninezero.cream.utils.nonSpatialExpressiveSpring
import com.ninezero.cream.utils.spatialExpressiveSpring
import com.ninezero.cream.viewmodel.MainViewModel

val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

@Composable
fun MainScreen() {
    //val viewModel = hiltViewModel<MainViewModel>()
    val navController = rememberCreamNavController()

    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this
        ) {
            NavHost(
                navController = navController.navController,
                startDestination = AppRoutes.MAIN
            ) {
                composableWithCompositionLocal(route = AppRoutes.MAIN) {
                    MainContent(
                        onCartClick = navController::navigateToCart,
                        onSearchClick = navController::navigateToSearch,
                        onCategoryClick = navController::navigateToCategoryDetail
                    )
                }

                composableWithCompositionLocal(
                    route = "${AppRoutes.CATEGORY_DETAIL}/{${AppRoutes.CATEGORY_ID_KEY}}/{${AppRoutes.CATEGORY_NAME_KEY}}",
                    arguments = listOf(
                        navArgument(AppRoutes.CATEGORY_ID_KEY) { type = NavType.StringType },
                        navArgument(AppRoutes.CATEGORY_NAME_KEY) { type = NavType.StringType }
                    )
                ) {
                    val arguments = requireNotNull(it.arguments)
                    val categoryId = arguments.getString(AppRoutes.CATEGORY_ID_KEY) ?: ""
                    val categoryName = arguments.getString(AppRoutes.CATEGORY_NAME_KEY) ?: ""

                    CategoryDetailScreen(
                        categoryId = categoryId,
                        categoryName = categoryName,
                        onProductClick = { productId ->
                            navController.navigateToProductDetail(productId = productId, it)
                        },
                        onNavigateBack = navController::navigateBack
                    )
                }
            }
        }
    }
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    onCartClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCategoryClick: (String, String, NavBackStackEntry) -> Unit
) {
    val creamScaffoldState = rememberCreamScaffoldState()
    val nestedNavController = rememberCreamNavController()
    val navBackStackEntry by nestedNavController.navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No SharedElementScope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No SharedElementScope found")

    CreamScaffold(
        bottomBar = {
            with(animatedVisibilityScope) {
                with(sharedTransitionScope) {
                    BottomBar(
                        currentRoute = currentRoute ?: AppRoutes.MAIN_HOME,
                        navigateToRoute = nestedNavController::navigateToBottomBarRoute,
                        modifier = Modifier
                            .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1f)
                            .animateEnterExit(
                                enter = fadeIn(
                                    nonSpatialExpressiveSpring()
                                ) + slideInVertically(
                                    spatialExpressiveSpring()
                                ) { it },
                                exit = fadeOut(
                                    nonSpatialExpressiveSpring()
                                ) + slideOutVertically(
                                    spatialExpressiveSpring()
                                ) { it }
                            )
                    )
                }
            }
        },
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                hostState = it,
                modifier = Modifier.systemBarsPadding(),
                snackbar = { snackbarData -> CustomSnackbar(snackbarData) }
            )
        },
        snackbarHostState = creamScaffoldState.snackBarHostState
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController.navController,
            startDestination = AppRoutes.MAIN_HOME
        ) {
            addMainGraph(
                onCartClick = onCartClick,
                onSearchClick = onSearchClick,
                onCategoryClick = onCategoryClick,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            )
        }
    }
}


