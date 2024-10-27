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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.ninezero.cream.ui.auth.AuthState
import com.ninezero.cream.ui.auth.LoginScreen
import com.ninezero.cream.ui.cart.CartScreen
import com.ninezero.cream.ui.product_detail.ProductDetailScreen
import com.ninezero.cream.ui.category_detail.CategoryDetailScreen
import com.ninezero.cream.ui.component.BottomBar
import com.ninezero.cream.ui.component.CreamScaffold
import com.ninezero.cream.ui.navigation.Routes
import com.ninezero.cream.ui.navigation.addMainGraph
import com.ninezero.cream.ui.navigation.composableWithCompositionLocal
import com.ninezero.cream.ui.navigation.rememberAppNavController
import com.ninezero.cream.utils.nonSpatialExpressiveSpring
import com.ninezero.cream.utils.rememberProductDetailAnimState
import com.ninezero.cream.utils.spatialExpressiveSpring
import com.ninezero.cream.viewmodel.MainViewModel

val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

@Composable
fun MainScreen() {
    val viewModel = hiltViewModel<MainViewModel>()
    val navController = rememberAppNavController()
    var navigateToSaved by remember { mutableStateOf(false) }
    val animState = rememberProductDetailAnimState()
    val authState by viewModel.authState.collectAsState()

    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavHost(
                navController = navController.navController,
                startDestination = Routes.MAIN
            ) {
                composableWithCompositionLocal(route = Routes.MAIN) {
                    MainContent(
                        onCartClick = {
                            when (authState) {
                                AuthState.AUTHENTICATED -> navController.navigateToCart()
                                AuthState.UNAUTHENTICATED -> navController.navigateToLogin()
                            }
                        },
                        onProductClick = { productId -> navController.navigateToProductDetail(productId = productId, it) },
                        onCategoryClick = navController::navigateToCategoryDetail,
                        onNavigateToLogin = navController::navigateToLogin,
                        navigateToSaved = navigateToSaved,
                        onNavigateToSavedConsumed = { navigateToSaved = false },
                        authState = authState,
                        onSignOut = {
                            viewModel.setAuthState(AuthState.UNAUTHENTICATED)
                            navController.navigateToMain()
                        }
                    )
                }

                composableWithCompositionLocal(
                    route = Routes.CART,
                    enterTransition = animState.first,
                    exitTransition = animState.second
                ) {
                    CartScreen(
                        onProductClick = { productId -> navController.navigateToProductDetail(productId = productId, it) },
                        onNavigateBack = navController::navigateBack,
                        onNavigateToHome = navController::navigateToMain
                    )
                }

                composableWithCompositionLocal(
                    route = Routes.LOGIN,
                    enterTransition = animState.first,
                    exitTransition = animState.second
                ) {
                    LoginScreen(
                        onNavigateBack = navController::navigateBack,
                        onLoginSuccess = {
                            viewModel.setAuthState(AuthState.AUTHENTICATED)
                            navController.navigateBack()
                        }
                    )
                }

                composableWithCompositionLocal(
                    route = Routes.productDetailRoute("{${Routes.PRODUCT_ID_KEY}}"),
                    arguments = listOf(navArgument(Routes.PRODUCT_ID_KEY) { type = NavType.StringType }),
                    enterTransition = animState.first,
                    exitTransition = animState.second
                ) {
                    ProductDetailScreen(
                        onCartClick = {
                            when (authState) {
                                AuthState.AUTHENTICATED -> navController.navigateToCart()
                                AuthState.UNAUTHENTICATED -> navController.navigateToLogin()
                            }
                        },
                        onProductClick = { productId -> navController.navigateToProductDetail(productId = productId, it) },
                        onNavigateBack = navController::navigateBack,
                        onNavigateToHome = navController::navigateToMain,
                        onNavigateToLogin = navController::navigateToLogin,
                        onNavigateToSaved = {
                            navigateToSaved = true
                            navController.navigateToMain()
                        }
                    )
                }

                composableWithCompositionLocal(
                    route = Routes.categoryDetailRoute(
                        "{${Routes.CATEGORY_ID_KEY}}",
                        "{${Routes.CATEGORY_NAME_KEY}}"
                    ),
                    arguments = listOf(
                        navArgument(Routes.CATEGORY_ID_KEY) { type = NavType.StringType },
                        navArgument(Routes.CATEGORY_NAME_KEY) { type = NavType.StringType }
                    )
                ) {
                    val arguments = requireNotNull(it.arguments)

                    CategoryDetailScreen(
                        categoryId = arguments.getString(Routes.CATEGORY_ID_KEY).orEmpty(),
                        categoryName = arguments.getString(Routes.CATEGORY_NAME_KEY).orEmpty(),
                        onProductClick = { productId -> navController.navigateToProductDetail(productId = productId, it) },
                        onNavigateBack = navController::navigateBack,
                        onNavigateToLogin = navController::navigateToLogin,
                        onNavigateToSaved = {
                            navigateToSaved = true
                            navController.navigateBack()
                        }
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
    onProductClick: (String) -> Unit,
    onCategoryClick: (String, String, NavBackStackEntry) -> Unit,
    onNavigateToLogin: () -> Unit,
    navigateToSaved: Boolean,
    onNavigateToSavedConsumed: () -> Unit,
    authState: AuthState,
    onSignOut: () -> Unit
) {
    val nestedNavController = rememberAppNavController()
    val navBackStackEntry by nestedNavController.navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No SharedElementScope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No SharedElementScope found")

    LaunchedEffect(navigateToSaved) {
        if (navigateToSaved) {
            nestedNavController.navigateToSaved()
            onNavigateToSavedConsumed()
        }
    }

    CreamScaffold(
        bottomBar = {
            with(animatedVisibilityScope) {
                with(sharedTransitionScope) {
                    BottomBar(
                        currentRoute = currentRoute ?: Routes.MAIN_HOME,
                        navigateToRoute = { route ->
                            when (route) {
                                Routes.MAIN_SAVED, Routes.MAIN_MY_PAGE -> {
                                    when (authState) {
                                        AuthState.AUTHENTICATED -> nestedNavController.navigateToBottomBarRoute(route)
                                        AuthState.UNAUTHENTICATED -> onNavigateToLogin()
                                    }
                                }
                                else -> nestedNavController.navigateToBottomBarRoute(route)
                            }
                        },
                        modifier = Modifier
                            .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1f)
                            .animateEnterExit(
                                enter = fadeIn(nonSpatialExpressiveSpring())
                                        + slideInVertically(spatialExpressiveSpring()) { it },
                                exit = fadeOut(nonSpatialExpressiveSpring())
                                        + slideOutVertically(spatialExpressiveSpring()) { it }
                            )
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController.navController,
            startDestination = Routes.MAIN_HOME,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            addMainGraph(
                onCartClick = onCartClick,
                onProductClick = onProductClick,
                onCategoryClick = onCategoryClick,
                onNavigateToHome = nestedNavController::navigateToHome,
                onNavigateToLogin = onNavigateToLogin,
                onNavigateToSaved = nestedNavController::navigateToSaved,
                onSignOut = onSignOut
            )
        }
    }
}


