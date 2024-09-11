package com.ninezero.cream.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ninezero.cream.ui.component.SingleBanner
import com.ninezero.cream.ui.component.BrandCard
import com.ninezero.cream.ui.component.Divider
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.GridSection
import com.ninezero.cream.ui.component.ProductCard
import com.ninezero.cream.ui.component.RowSection
import com.ninezero.cream.ui.component.SearchBar
import com.ninezero.cream.ui.component.TopBanner
import com.ninezero.cream.utils.collectAsState
import com.ninezero.cream.viewmodel.HomeViewModel
import com.ninezero.di.R
import com.ninezero.domain.model.HomeData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenSearch: () -> Unit,
    onOpenCart: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()

//    viewModel.collectEvents {
//        when (it) {
//            is HomeEvent.NavigateToProductDetail -> //onProductClick
//        }
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SearchBar(
                        onClick = onOpenSearch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                            .height(44.dp)
                    )
                },
                actions = {
                    IconButton(
                        onClick = onOpenCart,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_bag), contentDescription = "Cart")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        when (val state = uiState) {
            is HomeState.Loading -> HomeSkeleton(modifier = Modifier.padding(innerPadding))
            is HomeState.Content -> HomeContent(
                data = state.homeData,
                onProductClick = { /*TODO*/ },
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

@Composable
private fun HomeContent(
    data: HomeData,
    onProductClick: (String) -> Unit,
    onSaveClick: (String) -> Unit,
    onBrandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { TopBanner(banners = data.topBanners) }
        item {
            RowSection(
                title = stringResource(id = R.string.just_dropped),
                subtitle = stringResource(id = R.string.just_dropped_subtitle),
                products = data.justDropped,
                onProductClick = onProductClick,
                onSaveClick = onSaveClick
            )
        }
        item { Divider() }
        item {
            RowSection(
                title = stringResource(id = R.string.most_popular),
                subtitle = stringResource(id = R.string.most_popular_subtitle),
                products = data.mostPopular,
                onProductClick = onProductClick,
                onSaveClick = onSaveClick
            )
        }
        item { data.banner?.let { SingleBanner(banner = it) } }
        item {
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
        item { Divider() }
        item {
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