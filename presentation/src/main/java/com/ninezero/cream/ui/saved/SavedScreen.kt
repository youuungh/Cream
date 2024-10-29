package com.ninezero.cream.ui.saved

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ninezero.cream.base.collectEvents
import com.ninezero.cream.ui.component.CreamPullRefresh
import com.ninezero.cream.ui.component.CreamScaffold
import com.ninezero.cream.ui.component.CreamSurface
import com.ninezero.cream.ui.component.CreamTopAppBar
import com.ninezero.cream.ui.component.Divider
import com.ninezero.cream.ui.component.EmptyScreen
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.SavedProductCard
import com.ninezero.cream.ui.component.bottomsheet.BottomSheetState
import com.ninezero.cream.ui.component.bottomsheet.CreamBottomSheet
import com.ninezero.cream.ui.component.skeleton.SavedSkeleton
import com.ninezero.cream.utils.SavedSortOption
import com.ninezero.cream.viewmodel.SavedViewModel
import com.ninezero.di.R
import com.ninezero.domain.model.Product

@Composable
fun SavedScreen(
    modifier: Modifier = Modifier,
    onCartClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SavedViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    val isRefresh by viewModel.isRefresh.collectAsState()
    val scope = rememberCoroutineScope()
    val showBottomSheet = remember { mutableStateOf(false) }

    viewModel.collectEvents {
        when (it) {
            is SavedEvent.NavigateToProductDetail -> onProductClick(it.productId)
        }
    }

    CreamSurface(modifier = Modifier.fillMaxSize()) {
        CreamScaffold(
            topBar = {
                CreamTopAppBar(
                    title = stringResource(R.string.main_saved),
                    onCartClick = onCartClick
                )
            }
        ) { innerPadding ->
            CreamPullRefresh(
                refreshing = isRefresh,
                onRefresh = { viewModel.refreshData() }
            ) {
                SavedScreenContent(
                    uiState = uiState,
                    sortType = sortType,
                    innerPadding = innerPadding,
                    onNavigateToHome = onNavigateToHome,
                    onProductClick = onProductClick,
                    onRemoveClick = { product -> viewModel.action(SavedAction.Remove(product)) },
                    onSortClick = { showBottomSheet.value = true },
                    onRetry = { viewModel.action(SavedAction.Fetch) },
                    modifier = modifier
                )
            }
        }

        if (showBottomSheet.value) {
            CreamBottomSheet(
                showBottomSheet = showBottomSheet,
                state = BottomSheetState.SavedSort(
                    selectedOption = sortType,
                    onOptionSelected = { option -> viewModel.action(SavedAction.UpdateSortType(option)) }
                ),
                onDismiss = { showBottomSheet.value = false },
                coroutineScope = scope
            )
        }
    }
}

@Composable
private fun SavedScreenContent(
    uiState: SavedState,
    sortType: SavedSortOption,
    innerPadding: PaddingValues,
    onNavigateToHome: () -> Unit,
    onProductClick: (String) -> Unit,
    onRemoveClick: (Product) -> Unit,
    onSortClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is SavedState.Fetching -> SavedSkeleton(modifier = modifier.padding(innerPadding))
        is SavedState.Content -> {
            if (uiState.products.isEmpty()) {
                EmptyScreen(
                    onNavigateToHome = onNavigateToHome,
                    title = stringResource(id = R.string.no_saved_items),
                    label = stringResource(id = R.string.view_home),
                    modifier = modifier.padding(innerPadding)
                )
            } else {
                SavedContent(
                    savedProducts = uiState.products,
                    sortType = sortType,
                    onProductClick = onProductClick,
                    onRemoveClick = onRemoveClick,
                    onSortClick = onSortClick,
                    modifier = modifier.padding(innerPadding)
                )
            }
        }
        is SavedState.Error -> ErrorScreen(
            onRetry = onRetry,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SavedContent(
    savedProducts: List<Product>,
    sortType: SavedSortOption,
    onProductClick: (String) -> Unit,
    onRemoveClick: (Product) -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item(key = "header") {
            SavedHeader(
                itemCount = savedProducts.size,
                sortType = sortType,
                onSortClick = onSortClick
            )
        }

        animatedItems(
            items = savedProducts,
            onProductClick = onProductClick,
            onRemoveClick = onRemoveClick
        )
    }
}

@Composable
private fun SavedHeader(
    itemCount: Int,
    sortType: SavedSortOption,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.total_items, itemCount),
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Normal
            )
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onSortClick)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(sortType.stringResId),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Normal
                )
            )
            Icon(
                painter = painterResource(R.drawable.ic_sort),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

fun LazyListScope.animatedItems(
    items: List<Product>,
    onProductClick: (String) -> Unit,
    onRemoveClick: (Product) -> Unit
) {
    itemsIndexed(
        items = items,
        key = { _, product -> product.productId }
    ) { index, product ->
        SavedProductCard(
            product = product,
            onClick = { onProductClick(product.productId) },
            onSaveToggle = onRemoveClick,
            modifier = Modifier
                .height(130.dp)
                .animateItem(
                    fadeInSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    fadeOutSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    placementSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
            )
        )

        if (index < items.size - 1) { Divider() }
    }
}