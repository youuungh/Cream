package com.ninezero.cream.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ninezero.cream.ui.category.CategoryDetailScreen
import com.ninezero.cream.ui.category.CategoryScreen
import com.ninezero.cream.ui.component.SearchBar
import com.ninezero.cream.ui.component.Snackbar
import com.ninezero.cream.ui.home.HomeScreen
import com.ninezero.cream.utils.NavUtils
import com.ninezero.cream.viewmodel.MainViewModel
import com.ninezero.di.R

@Composable
fun MainScreen() {
    val viewModel = hiltViewModel<MainViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (MainRoute.isMainRoute(currentRoute))
                BottomNavigationBar(navController, currentRoute)
        },
        snackbarHost = { Snackbar(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MainNavHost(
                navController = navController,
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController, currentRoute: String?) {
    val bottomNavigationRoutes = listOf(
        MainRoute.Home,
        MainRoute.Category,
        MainRoute.Saved,
        MainRoute.MyPage
    )

    BottomAppBar(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
    ) {
        bottomNavigationRoutes.forEach {
            NavigationBarItem(
                icon = { Icon(painter = it.icon(), contentDescription = it.title) },
                alwaysShowLabel = false,
                label = { Text(it.title) },
                selected = currentRoute == it.route,
                onClick = {
                    NavUtils.navigateTo(
                        controller = navController,
                        destination = it.route,
                        popUpToRoute = navController.graph.findStartDestination().route
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedTextColor = MaterialTheme.colorScheme.outline,
                    unselectedIconColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
private fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainRoute.Home.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        composable(
            route = MainRoute.Home.route,
            deepLinks = MainRoute.Home.deepLinks
        ) {
            HomeScreen(
                viewModel = hiltViewModel(),
                onOpenSearch = { viewModel.openSearch(navController) },
                onOpenCart = { viewModel.openCart(navController) }
            )
        }
        composable(
            route = MainRoute.Category.route,
            deepLinks = MainRoute.Category.deepLinks
        ) {
            CategoryScreen(
                viewModel = hiltViewModel(),
                onOpenCart = { viewModel.openCart(navController) },
                onCategoryClick = { navController.navigate(CategoryDetailRoute.navigateWithArg(it)) }
            )
        }
        composable(
            route = MainRoute.Saved.route,
            deepLinks = MainRoute.Saved.deepLinks
        ) {
            // Saved
        }
        composable(
            route = MainRoute.MyPage.route,
            deepLinks = MainRoute.MyPage.deepLinks
        ) {
            // MyPage
        }
        composable(
            route = CartRoute.route,
            deepLinks = CartRoute.deepLinks
        ) {
            // Cart
        }
        composable(
            route = SearchRoute.route,
            deepLinks = SearchRoute.deepLinks
        ) {
            // Search
        }
        composable(
            route = CategoryDetailRoute.routeWithArgName(),
            arguments = CategoryDetailRoute.arguments
        ) {
            CategoryDetailScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onProductClick = { // ProductDetail
                }
            )
        }
    }
}