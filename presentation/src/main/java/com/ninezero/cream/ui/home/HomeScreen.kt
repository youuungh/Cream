@file:OptIn(ExperimentalSharedTransitionApi::class)
package com.ninezero.cream.ui.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ninezero.cream.ui.component.SingleBanner
import com.ninezero.cream.ui.component.BrandCard
import com.ninezero.cream.ui.component.Divider
import com.ninezero.cream.ui.component.GridSection
import com.ninezero.cream.ui.component.ProductCard
import com.ninezero.cream.ui.component.RowSection
import com.ninezero.cream.base.collectAsState
import com.ninezero.cream.base.collectEvents
import com.ninezero.cream.ui.component.CreamSurface
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.SearchTopAppBar
import com.ninezero.cream.ui.component.TopBanner
import com.ninezero.cream.ui.component.skeleton.HomeSkeleton
import com.ninezero.cream.viewmodel.HomeViewModel
import com.ninezero.di.R
import com.ninezero.domain.model.HomeData
import timber.log.Timber

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onCartClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsState()
    val networkState by viewModel.networkState.collectAsState()

    viewModel.collectEvents {
        when (it) {
            is HomeEvent.NavigateToProductDetail -> onProductClick(it.productId)
        }
    }

    LaunchedEffect(networkState) {
        Timber.d("networkState: $networkState")
    }

    CreamSurface(modifier = modifier.fillMaxSize()) {
        SharedTransitionLayout {
            Scaffold(
                topBar = {
                    SearchTopAppBar(onCartClick = onCartClick, onSearchClick = onSearchClick)
                }
            ) { innerPadding ->
                when (val state = uiState) {
                    is HomeState.Loading -> HomeSkeleton(
                        modifier = Modifier.padding(innerPadding)
                    )

                    is HomeState.Content -> HomeContent(
                        data = state.homeData,
                        onProductClick = { productId -> viewModel.action(HomeAction.ProductClicked(productId)) },
                        onSaveClick = { /*TODO*/ },
                        onBrandClick = { /*TODO*/ },
                        modifier = Modifier.padding(innerPadding)
                    )

                    is HomeState.Error -> ErrorScreen(
                        onRetry = { viewModel.action(HomeAction.Refresh) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    data: HomeData,
    onProductClick: (String) -> Unit,
    onSaveClick: (String) -> Unit,
    onBrandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { TopBanner(banners = data.topBanners) }
        item {
            key("just_dropped") {
                RowSection(
                    title = stringResource(id = R.string.just_dropped),
                    subtitle = stringResource(id = R.string.just_dropped_subtitle),
                    products = data.justDropped,
                    onProductClick = onProductClick,
                    onSaveClick = onSaveClick
                )
            }
        }
        item { Divider() }
        item {
            key("most_popular") {
                RowSection(
                    title = stringResource(id = R.string.most_popular),
                    subtitle = stringResource(id = R.string.most_popular_subtitle),
                    products = data.mostPopular,
                    onProductClick = onProductClick,
                    onSaveClick = onSaveClick
                )
            }
        }
        item { data.banner?.let { key(it.bannerId) { SingleBanner(banner = it) } } }
        item {
            key("for_you") {
                GridSection(
                    title = stringResource(id = R.string.for_you),
                    subtitle = stringResource(id = R.string.for_you_subtitle),
                    items = data.forYou,
                    rows = 2,
                    height = 550
                ) {
                    ProductCard(
                        product = it,
                        onClick = { onProductClick(it.productId) },
                        onSaveClick = { /*TODO*/ }
                    )
                }
            }
        }
        item { Divider() }
        item {
            key("top_brand") {
                GridSection(
                    title = stringResource(id = R.string.top_brand),
                    subtitle = stringResource(id = R.string.top_brand_subtitle),
                    items = data.brands,
                    rows = 3,
                    height = 260
                ) {
                    BrandCard(
                        brand = it,
                        onClick = { onBrandClick(it.brandId) }
                    )
                }
            }
        }
    }
}