package com.ninezero.cream.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ninezero.cream.ui.category.CategoryScreen
import com.ninezero.cream.ui.home.HomeScreen
import com.ninezero.cream.ui.theme.CreamTheme
import com.ninezero.cream.utils.NavigationUtils
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
        topBar = {
            MainHeader(
                viewModel = viewModel,
                navController = navController,
                currentRoute = currentRoute
            )
        },
        bottomBar = {
            if (MainRoute.isMainRoute(currentRoute))
                BottomNavigationBar(navController, currentRoute)
        },
        snackbarHost = { CustomRoundedSnackbar(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MainContent(
                navController = navController,
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
                    NavigationUtils.navigateTo(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainHeader(
    viewModel: MainViewModel,
    navController: NavHostController,
    currentRoute: String?
) {
    TopAppBar(
        title = {
            when {
                currentRoute == MainRoute.Home.route -> {
                    // Home
                    ClickableSearchBar(
                        onClick = {
                            //navController.navigate(SearchRoute.route)
                            viewModel.openSearch(navController)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                            .height(44.dp)
                    )
                }
                MainRoute.isMainRoute(currentRoute) -> {
                    // Category, Saved, MyPage
                    Text(
                        text = NavigationUtils.getDestinationFromRoute(currentRoute).title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                else -> {
                    // Extra
                    Text(
                        text = NavigationUtils.getDestinationFromRoute(currentRoute).title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        },
        navigationIcon = {
            if (currentRoute == CartRoute.route || !MainRoute.isMainRoute(currentRoute)) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { viewModel.openCart(navController) },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(painter = painterResource(R.drawable.ic_cart), contentDescription = "Cart")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun ClickableSearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "브랜드, 상품 등",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun MainContent(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainRoute.Home.route,
        modifier = modifier
    ) {
        composable(
            route = MainRoute.Home.route,
            deepLinks = MainRoute.Home.deepLinks
        ) {
            HomeScreen(viewModel = hiltViewModel(), navController = navController)
        }
        composable(
            route = MainRoute.Category.route,
            deepLinks = MainRoute.Category.deepLinks
        ) {
            CategoryScreen(navController = navController)
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
    }
}

@Composable
fun CustomRoundedSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    cornerRadius: Int = 10
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier.padding(16.dp),
        snackbar = {
            Snackbar(
                snackbarData = it,
                shape = RoundedCornerShape(cornerRadius.dp)
            )
        }
    )
}