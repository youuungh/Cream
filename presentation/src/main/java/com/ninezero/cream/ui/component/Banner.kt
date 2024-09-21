package com.ninezero.cream.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ninezero.domain.model.Banner
import com.ninezero.domain.model.TopBanner
import kotlinx.coroutines.delay

@Composable
fun TopBanner(banners: List<TopBanner>) {
    val maxPages = Int.MAX_VALUE
    val initPage = remember { maxPages / 2 - (maxPages / 2) % banners.size }
    val pagerState = rememberPagerState(initialPage = initPage) { maxPages }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val indicatorWidth = remember { (screenWidth - 32.dp) / banners.size }
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    var autoScroll by remember { mutableStateOf(true) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            autoScroll = event == Lifecycle.Event.ON_RESUME
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(isDragged, autoScroll) {
        if (!isDragged && autoScroll) {
            while (true) {
                delay(3000)
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            key = { banners[it % banners.size].bannerId }
        ) { page ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(banners[page % banners.size].imageUrl.firstOrNull())
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        IndicatorBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            pagerState = pagerState,
            bannerCount = banners.size,
            indicatorWidth = indicatorWidth
        )
    }
}

@Composable
private fun IndicatorBar(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    bannerCount: Int,
    indicatorWidth: Dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .height(2.dp)
            .background(Color.White.copy(alpha = 0.3f))
    ) {
        val currentPage = pagerState.currentPage % bannerCount
        val indicatorOffset by animateDpAsState(
            targetValue = currentPage * indicatorWidth,
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

@Composable
fun SingleBanner(banner: Banner) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(vertical = 16.dp)
    ) {
        AsyncImage(
            model = banner.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}