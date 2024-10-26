package com.ninezero.cream.ui.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ninezero.cream.utils.RECOMMEND_INFO_INDEX
import com.ninezero.cream.utils.STYLE_INFO_INDEX
import com.ninezero.cream.utils.TAB_KEY
import com.ninezero.cream.utils.TAB_OVERLAP

sealed interface TabScrollState {
    object Hidden : TabScrollState
    data class Visible(val alpha: Float) : TabScrollState
}

enum class TabSection(val index: Int) {
    STYLE(0),
    RECOMMEND(1);

    companion object {
        fun fromIndex(index: Int) = entries.firstOrNull { it.index == index } ?: STYLE
    }
}

class DetailTabState(
    private val density: Density,
    private val lazyListState: LazyListState,
    private val appBarHeight: Dp,
    initialTabHeight: Dp = 0.dp,
    initialSelectedIndex: Int = 0
) {
    var tabHeight by mutableStateOf(initialTabHeight)
        private set
    var selectedTabIndex by mutableIntStateOf(initialSelectedIndex)
        private set

    val tabVisibility: TabScrollState by derivedStateOf {
        val layoutInfo = lazyListState.layoutInfo
        val visibleItemsInfo = layoutInfo.visibleItemsInfo
        val tabItem = visibleItemsInfo.find { it.key == TAB_KEY }
        val tabIndex = layoutInfo.totalItemsCount - 3

        when {
            tabItem == null && lazyListState.firstVisibleItemIndex >= tabIndex ->
                TabScrollState.Visible(1f)
            tabItem == null ->
                TabScrollState.Hidden
            tabItem.offset + tabItem.size <= with(density) {
                (appBarHeight + tabHeight + TAB_OVERLAP.dp).toPx()
            } -> TabScrollState.Visible(1f)
            else -> TabScrollState.Hidden
        }
    }

    val currentTabSection: TabSection by derivedStateOf {
        val adjustmentPx = with(density) {
            (appBarHeight + tabHeight + TAB_OVERLAP.dp).toPx()
        }
        val visibleItemsInfo = lazyListState.layoutInfo.visibleItemsInfo

        val styleInfoItem = visibleItemsInfo.find { it.index == STYLE_INFO_INDEX }
        val recommendInfoItem = visibleItemsInfo.find { it.index == RECOMMEND_INFO_INDEX }

        when {
            recommendInfoItem != null && recommendInfoItem.offset - adjustmentPx <= 0 ->
                TabSection.RECOMMEND
            styleInfoItem != null && styleInfoItem.offset - adjustmentPx <= 0 ->
                TabSection.STYLE
            else -> TabSection.fromIndex(selectedTabIndex)
        }
    }

    suspend fun animateToSection(section: TabSection) {
        lazyListState.animateScrollToItem(
            index = getScrollPosition(section),
            scrollOffset = -calculateScrollOffset()
        )
    }

    private fun getScrollPosition(section: TabSection): Int = when (section) {
        TabSection.STYLE -> STYLE_INFO_INDEX
        TabSection.RECOMMEND -> RECOMMEND_INFO_INDEX
    }

    private fun calculateScrollOffset(): Int = with(density) {
        (appBarHeight + tabHeight).toPx().toInt()
    }

    fun updateTabHeight(height: Dp) {
        tabHeight = height
    }

    fun updateSelectedIndex(index: Int) {
        selectedTabIndex = index
    }
}

fun DetailTabState.getVisibilityModifier(): Float = when (val visibility = tabVisibility) {
    is TabScrollState.Visible -> visibility.alpha
    TabScrollState.Hidden -> 0f
}