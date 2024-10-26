package com.ninezero.cream.ui.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.ninezero.cream.utils.IMAGE_HEIGHT
import com.ninezero.cream.utils.MAX_CORNER_RADIUS
import com.ninezero.cream.utils.SCROLL_THRESHOLD_OFFSET

@Composable
fun rememberAppBarAlphaState(lazyListState: LazyListState): State<Float> {
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
fun rememberContentCornerRadiusState(lazyListState: LazyListState): State<Dp> {
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