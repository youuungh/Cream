package com.ninezero.cream.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.ninezero.cream.base.collectAsState
import com.ninezero.cream.utils.BANNER_DELAY
import com.ninezero.cream.utils.BANNER_DURATION
import com.ninezero.cream.utils.BANNER_HEIGHT
import com.ninezero.domain.model.Banner
import com.ninezero.domain.model.TopBanner
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TopBanner(banners: List<TopBanner>) {
    val bannerCount = banners.size
    val maxPageCount = Int.MAX_VALUE
    val initPage = remember { maxPageCount / 2 - (maxPageCount / 2) % bannerCount }
    val pagerState = rememberPagerState(initialPage = initPage) { maxPageCount }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val indicatorWidth = remember { (screenWidth - 32.dp) / banners.size }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    var isUserInteracting by remember { mutableStateOf(false) }
    var isAutoScrolling by remember { mutableStateOf(false) }
    var autoScrollJob by remember { mutableStateOf<Job?>(null) }

    fun startAutoScroll() {
        autoScrollJob?.cancel()
        autoScrollJob = coroutineScope.launch {
            while (true) {
                delay(BANNER_DELAY)
                if (!isUserInteracting) {
                    isAutoScrolling = true
                    val nextPage = (pagerState.currentPage + 1) % maxPageCount
                    pagerState.animateScrollToPage(
                        page = nextPage,
                        animationSpec = tween(durationMillis = BANNER_DURATION, easing = FastOutSlowInEasing)
                    )
                    isAutoScrolling = false
                }
            }
        }
    }

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                pagerState.scrollToPage(pagerState.currentPage)
                startAutoScroll()
            }
            else -> autoScrollJob?.cancel()
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }.collect { isScrolling ->
            if (!isAutoScrolling) isUserInteracting = isScrolling
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
                .height(BANNER_HEIGHT.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isUserInteracting = true
                            tryAwaitRelease()
                            isUserInteracting = false
                        }
                    )
                },
            key = { it },
            beyondViewportPageCount = 1
        ) { page ->
            val bannerIndex = remember(page) { page.mod(bannerCount) }
            TopBannerImage(banners[bannerIndex])
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
            animationSpec = spring(stiffness = Spring.StiffnessLow),
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