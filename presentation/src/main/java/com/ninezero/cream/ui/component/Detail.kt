@file:OptIn(ExperimentalFoundationApi::class)

package com.ninezero.cream.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import com.ninezero.cream.ui.product.ProductDetailState
import com.ninezero.cream.utils.CONTENT_OVERLAP
import com.ninezero.cream.utils.DETAIL_BOTTOM_BAR_HEIGHT
import com.ninezero.cream.utils.IMAGE_HEIGHT
import com.ninezero.cream.utils.MAX_CORNER_RADIUS
import com.ninezero.cream.utils.NumUtils.formatPriceWithCommas
import com.ninezero.cream.utils.ProductDetailTab
import com.ninezero.cream.utils.SCROLL_THRESHOLD_OFFSET
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun ProductDetailContent(
    state: ProductDetailState.Content,
    onProductClick: (String) -> Unit,
    onSaveToggle: () -> Unit,
    onBuyClick: () -> Unit,
    updateAppBarAlpha: (Float) -> Unit,
    appBarHeight: Dp,
    tabVisible: Boolean
) {
    val lazyListState = rememberLazyListState()
    val appBarAlpha by rememberAppBarAlphaState(lazyListState)
    val contentCornerRadius by rememberContentCornerRadiusState(lazyListState)
    val density = LocalDensity.current

    val tabKey = "product_detail_tab"
    var tabHeight by remember { mutableStateOf(0.dp) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val tabVisibility by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            val tabItem = visibleItemsInfo.find { it.key == tabKey }
            val tabIndex = layoutInfo.totalItemsCount - 3

            when {
                tabItem == null && lazyListState.firstVisibleItemIndex >= tabIndex -> 1f
                tabItem == null -> 0f
                tabItem.offset + tabItem.size <= with(density) { (appBarHeight + CONTENT_OVERLAP.dp + tabHeight).toPx() } -> 1f
                else -> 0f
            }
        }
    }

    val scrollBasedTabIndex by remember {
        derivedStateOf {
            val adjustmentPx = with(density) { (appBarHeight + tabHeight + CONTENT_OVERLAP.dp).toPx() }
            val layoutInfo = lazyListState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo

            val styleInfoIndex = 3
            val recommendInfoIndex = 4

            val styleInfoItem = visibleItemsInfo.find { it.index == styleInfoIndex }
            val recommendInfoItem = visibleItemsInfo.find { it.index == recommendInfoIndex }

            when {
                recommendInfoItem != null && recommendInfoItem.offset - adjustmentPx <= 0 -> 1
                styleInfoItem != null && styleInfoItem.offset - adjustmentPx <= 0 -> 0
                else -> selectedTabIndex
            }
        }
    }

    LaunchedEffect(scrollBasedTabIndex) {
        if (scrollBasedTabIndex != selectedTabIndex) {
            selectedTabIndex = scrollBasedTabIndex
        }
    }

    LaunchedEffect(appBarAlpha) { updateAppBarAlpha(appBarAlpha) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize()
        ) {
            item { ProductDetailImage(imageUrl = state.product.imageUrl) }
            item {
                ProductDetailBody(
                    state = state,
                    contentCornerRadius = contentCornerRadius
                )
            }
            item(key = tabKey) {
                ProductDetailTabs(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { index ->
                        selectedTabIndex = index
                        coroutineScope.launch {
                            val adjustmentPx = with(density) { (appBarHeight + tabHeight).toPx() }.toInt()
                            lazyListState.animateScrollToItem(
                                index = when (index) {
                                    0 -> 3  // StyleInfo
                                    1 -> 4  // RecommendInfo
                                    else -> 3
                                },
                                scrollOffset = -adjustmentPx
                            )
                        }
                    },
                    modifier = Modifier
                        .offset(y = -CONTENT_OVERLAP.dp)
                        .onGloballyPositioned {
                            tabHeight = with(density) { it.size.height.toDp() }
                        },
                    visible = tabVisible
                )
            }
            item { StyleInfo() }
            item { RecommendInfo(state.relatedProducts, onProductClick, onSaveToggle) }
        }

        ProductDetailTabs(
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index ->
                selectedTabIndex = index
                coroutineScope.launch {
                    val adjustmentPx = with(density) { (appBarHeight + tabHeight).toPx() }.toInt()
                    lazyListState.animateScrollToItem(
                        index = when (index) {
                            0 -> 3  // StyleInfo
                            1 -> 4  // RecommendInfo
                            else -> 3
                        },
                        scrollOffset = -adjustmentPx
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .alpha(tabVisibility)
                .padding(top = appBarHeight),
            visible = tabVisible
        )

        ProductBottomBar(
            price = formatPriceWithCommas(state.product.price.instantBuyPrice),
            isSaved = state.product.isSaved,
            onSaveToggle = onSaveToggle,
            onBuyClick = onBuyClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ProductDetailBody(
    state: ProductDetailState.Content,
    contentCornerRadius: Dp
) {
    CreamSurface(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = -CONTENT_OVERLAP.dp)
            .clip(
                RoundedCornerShape(
                    topStart = contentCornerRadius,
                    topEnd = contentCornerRadius
                )
            ),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)
            ) {
                ProductInfoHeader(product = state.product)
                ProductInfoContainer(product = state.product)
                Divider()
                BenefitInfoContainer()
                Divider()
                ShippingInfoContainer()
            }
            ColorSpacer()
            BrandInfoContainer(brand = state.product.brand)
            ColorSpacer()
        }
    }
}

@Composable
fun ProductDetailTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean
) {
    val tabs = ProductDetailTab.entries.toList()

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        text = { Text(tab.title) },
                        selected = selectedTabIndex == index,
                        onClick = { onTabSelected(index) }
                    )
                }
            }
        }
    }
}


@Composable
private fun rememberAppBarAlphaState(lazyListState: LazyListState): State<Float> {
    val density = LocalDensity.current
    val imageHeightPx = with(density) { IMAGE_HEIGHT.dp.toPx() }
    val scrollThreshold = imageHeightPx - with(density) { SCROLL_THRESHOLD_OFFSET.dp.toPx() }

    return remember {
        derivedStateOf {
            val scrollOffset = lazyListState.firstVisibleItemIndex * imageHeightPx +
                    lazyListState.firstVisibleItemScrollOffset
            (scrollOffset / scrollThreshold).coerceIn(0f, 1f)
        }
    }
}

@Composable
private fun rememberContentCornerRadiusState(lazyListState: LazyListState): State<Dp> {
    val density = LocalDensity.current
    val imageHeightPx = with(density) { IMAGE_HEIGHT.dp.toPx() }
    val scrollThreshold = imageHeightPx - with(density) { SCROLL_THRESHOLD_OFFSET.dp.toPx() }

    return remember {
        derivedStateOf {
            val scrollOffset = lazyListState.firstVisibleItemIndex * imageHeightPx +
                    lazyListState.firstVisibleItemScrollOffset
            val progress = (scrollOffset / scrollThreshold).coerceIn(0f, 1f)
            (1 - progress) * MAX_CORNER_RADIUS.dp
        }
    }
}


