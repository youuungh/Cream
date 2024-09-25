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

    fun navigateBack() {
        navController.navigateUp()
    }

    fun navigateToCart() {
        navController.navigate(AppRoutes.CART)
    }

    fun navigateToSearch() {
        navController.navigate(AppRoutes.SEARCH)
    }

    fun navigateToBottomBarRoute(route: String) {
        if (route != navController.currentDestination?.route) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(findStartDestination(navController.graph).id) {
                    saveState = true
                }
            }
        }
    }

    fun navigateToCategoryDetail(
        categoryId: String,
        categoryName: String,
        from: NavBackStackEntry
    ) {
        if (from.lifecycleIsResumed()) {
            navController.navigate("${AppRoutes.CATEGORY_DETAIL}/$categoryId/$categoryName")
        }
    }

    fun navigateToProductDetail(productId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate("${AppRoutes.PRODUCT_DETAIL}/$productId")
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
    onSearchClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String, String, NavBackStackEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    composable(
        route = AppRoutes.MAIN_HOME,
        deepLinks = listOf(navDeepLink {
            uriPattern = "${AppRoutes.DEEP_LINK_SCHEME}${AppRoutes.MAIN_HOME}"
        })
    ) {
        HomeScreen(
            onCartClick = onCartClick,
            onSearchClick = onSearchClick,
            onProductClick = onProductClick,
            modifier = modifier
        )
    }

    composable(
        route = AppRoutes.MAIN_CATEGORY,
        deepLinks = listOf(navDeepLink {
            uriPattern = "${AppRoutes.DEEP_LINK_SCHEME}${AppRoutes.MAIN_CATEGORY}"
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
        route = AppRoutes.MAIN_SAVED,
        deepLinks = listOf(navDeepLink {
            uriPattern = "${AppRoutes.DEEP_LINK_SCHEME}${AppRoutes.MAIN_SAVED}"
        })
    ) {
        SavedScreen(
            onCartClick = onCartClick,
            onProductClick = onProductClick,
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