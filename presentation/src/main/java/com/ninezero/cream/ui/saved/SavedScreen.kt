@file:OptIn(ExperimentalMaterial3Api::class)

package com.ninezero.cream.ui.saved

import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ninezero.cream.base.collectAsState
import com.ninezero.cream.base.collectEvents
import com.ninezero.cream.ui.component.CreamSurface
import com.ninezero.cream.ui.component.CreamTopAppBar
import com.ninezero.cream.ui.component.Divider
import com.ninezero.cream.ui.component.EmptyScreen
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.SaveBottomSheet
import com.ninezero.cream.ui.component.SavedProductCard
import com.ninezero.cream.ui.component.skeleton.SavedScreenSkeleton
import com.ninezero.cream.viewmodel.SavedViewModel
import com.ninezero.di.R
import com.ninezero.domain.model.Product
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()
    val showBottomSheet = remember { mutableStateOf(false) }

    viewModel.collectEvents {
        when (it) {
            is SavedEvent.NavigateToProductDetail -> onProductClick(it.productId)
        }
    }

    CreamSurface(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CreamTopAppBar(
                    title = stringResource(R.string.main_saved),
                    onCartClick = onCartClick
                )
            }
        ) { innerPadding ->
            when (val state = uiState) {
                is SavedState.Fetching -> SavedScreenSkeleton(modifier = Modifier.padding(innerPadding))

                is SavedState.Content -> {
                    if (state.savedProducts.isEmpty()) {
                        EmptyScreen(
                            onNavigateToHome = onNavigateToHome,
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        SavedContent(
                            savedProducts = state.savedProducts,
                            sortType = sortType,
                            onProductClick = onProductClick,
                            onRemoveClick = { product -> viewModel.action(SavedAction.Remove(product)) },
                            onSortClick = { showBottomSheet.value = true },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
                is SavedState.Error -> ErrorScreen(
                    onRetry = { viewModel.action(SavedAction.Refresh) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        if (showBottomSheet.value) {
            SaveBottomSheet(
                showBottomSheet = showBottomSheet,
                coroutineScope = scope,
                selectedOption = remember { mutableIntStateOf(sortType) },
                onOptionSelected = { viewModel.updateSortType(it) }
            )
        }
    }
}

@Composable
fun SavedContent(
    savedProducts: List<Product>,
    @StringRes sortType: Int,
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
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.total_items, savedProducts.size),
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
                        stringResource(sortType),
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

        animatedItems(
            items = savedProducts,
            onProductClick = onProductClick,
            onRemoveClick = onRemoveClick
        )
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