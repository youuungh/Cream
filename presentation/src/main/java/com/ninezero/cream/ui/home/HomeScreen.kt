package com.ninezero.cream.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.SectionTitle
import com.ninezero.cream.utils.collectAsState
import com.ninezero.cream.viewmodel.HomeViewModel
import com.ninezero.di.R
import com.ninezero.domain.model.Banner
import com.ninezero.domain.model.HomeData
import com.ninezero.domain.model.Product
import com.ninezero.domain.model.TopBanner

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {
    val uiState = viewModel.state.collectAsState()

    when (val state = uiState.value) {
        is HomeState.Loading -> LoadingIndicator()
        is HomeState.Content -> HomeContent(data = state.homeData) {
            // click
        }

        is HomeState.Error -> ErrorScreen { viewModel.action(HomeAction.Refresh) }
    }
}

@Composable
private fun HomeContent(
    data: HomeData, onProductClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            HomeTopBanner(banners = data.topBanners)
        }
        item {
            HomeProduct(
                title = stringResource(id = R.string.just_dropped),
                subtitle = stringResource(id = R.string.just_dropped_subtitle),
                products = data.justDropped,
                onProductClick = onProductClick
            )
        }
        item {
            HomeProduct(
                title = stringResource(id = R.string.most_popular),
                subtitle = stringResource(id = R.string.most_popular_subtitle),
                products = data.mostPopular,
                onProductClick = onProductClick
            )
        }
        item {
            data.banner?.let { HomeBanner(banner = it) }
        }
        item {
            HomeProduct(
                title = stringResource(id = R.string.for_you),
                subtitle = stringResource(id = R.string.for_you_subtitle),
                products = data.forYou,
                onProductClick = onProductClick
            )
        }
    }
}

@Composable
private fun HomeTopBanner(banners: List<TopBanner>) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val indicatorWidth = (screenWidth - 32.dp) / banners.size

    Box(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) { page ->
            AsyncImage(
                model = banners[page].imageUrl.firstOrNull(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .height(2.dp)
                .background(Color.White.copy(alpha = 0.3f))
        ) {
            val indicatorOffset by animateDpAsState(
                targetValue = pagerState.currentPage * indicatorWidth,
                animationSpec = tween(durationMillis = 150),
                label = "indicator_offset"
            )

            Box(
                Modifier
                    .offset(x = indicatorOffset)
                    .width(indicatorWidth)
                    .fillMaxHeight()
                    .background(Color.White)
            )
        }
    }
}


@Composable
private fun HomeBanner(banner: Banner) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 16.dp)
    ) {
        AsyncImage(
            model = banner.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun HomeProduct(
    title: String,
    subtitle: String,
    products: List<Product>,
    onProductClick: (String) -> Unit
) {
    Column {
        SectionTitle(title = title, subtitle = subtitle)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) {
                ProductCard(product = it, onClick = { onProductClick(it.productId) })
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Transparent)
            .clickable(onClick = onClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.brand.brandName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.productName,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${product.price.instantBuyPrice}원",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "즉시구매가",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}