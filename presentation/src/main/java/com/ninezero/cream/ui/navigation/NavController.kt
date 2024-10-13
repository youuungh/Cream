package com.ninezero.cream.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.ninezero.cream.ui.LocalNavAnimatedVisibilityScope
import com.ninezero.cream.ui.category.CategoryScreen
import com.ninezero.cream.ui.home.HomeScreen
import com.ninezero.cream.ui.saved.SavedScreen
import com.ninezero.cream.utils.nonSpatialExpressiveSpring

@Composable
fun rememberAppNavController(
    navController: NavHostController = rememberNavController()
): AppNavController = remember(navController) {
    AppNavController(navController)
}

@Stable
class AppNavController(val navController: NavHostController) {

    fun navigateBack() = navController.navigateUp()

    fun navigateToMain() {
        navController.navigate(Routes.MAIN) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    fun navigateToBottomBarRoute(route: String) {
        if (route != navController.currentDestination?.route) {
            navController.navigate(route) {
                popUpTo(findStartDestination(navController.graph).id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    fun navigateToHome() = navigateToBottomBarRoute(Routes.MAIN_HOME)
    fun navigateToSaved() = navigateToBottomBarRoute(Routes.MAIN_SAVED)
    fun navigateToCart() = navController.navigate(Routes.CART)

    fun navigateToCategoryDetail(categoryId: String, categoryName: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(Routes.categoryDetailRoute(categoryId, categoryName))
        }
    }

    fun navigateToProductDetail(productId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(Routes.productDetailRoute(productId)) {
                popUpTo("${Routes.PRODUCT_DETAIL}/{${Routes.PRODUCT_ID_KEY}}") {
                    inclusive = true
                }
            }
        }
    }
}

fun NavGraphBuilder.composableWithCompositionLocal(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = {
        fadeIn(nonSpatialExpressiveSpring())
    },
    exitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = {
        fadeOut(nonSpatialExpressiveSpring())
    },
    popEnterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
    popExitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route,
        arguments,
        deepLinks,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition
    ) {
        CompositionLocalProvider(
            LocalNavAnimatedVisibilityScope provides this@composable
        ) {
            content(it)
        }
    }
}

fun NavGraphBuilder.addMainGraph(
    onCartClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String, String, NavBackStackEntry) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    composable(
        route = Routes.MAIN_HOME,
        deepLinks = listOf(navDeepLink {
            uriPattern = "${Routes.DEEP_LINK_SCHEME}${Routes.MAIN_HOME}"
        })
    ) {
        HomeScreen(
            onCartClick = onCartClick,
            onProductClick = onProductClick,
            onNavigateToSaved = onNavigateToSaved,
            modifier = modifier
        )
    }

    composable(
        route = Routes.MAIN_CATEGORY,
        deepLinks = listOf(navDeepLink {
            uriPattern = "${Routes.DEEP_LINK_SCHEME}${Routes.MAIN_CATEGORY}"
        })
    ) {
        CategoryScreen(
            onCategoryClick = { categoryId, categoryName ->
                onCategoryClick(
                    categoryId,
                    categoryName,
                    it
                )
            },
            onCartClick = onCartClick,
            modifier = modifier
        )
    }

    composable(
        route = Routes.MAIN_SAVED,
        deepLinks = listOf(navDeepLink {
            uriPattern = "${Routes.DEEP_LINK_SCHEME}${Routes.MAIN_SAVED}"
        })
    ) {
        SavedScreen(
            onCartClick = onCartClick,
            onProductClick = onProductClick,
            onNavigateToHome = onNavigateToHome,
            modifier = modifier
        )
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}