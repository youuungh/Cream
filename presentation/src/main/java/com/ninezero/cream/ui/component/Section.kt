@file:OptIn(ExperimentalLayoutApi::class)
package com.ninezero.cream.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ninezero.cream.utils.SearchSortOption
import com.ninezero.di.R
import com.ninezero.domain.model.Product

@Composable
fun RowSection(
    title: String,
    subtitle: String,
    products: List<Product>,
    onProductClick: (String) -> Unit,
    onSaveClick: (Product) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        SectionTitle(title = title, subtitle = subtitle)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) {
                ProductCard(
                    product = it,
                    onClick = { onProductClick(it.productId) },
                    onSaveToggle = { onSaveClick(it) },
                    modifier = Modifier.width(160.dp)
                )
            }
        }
    }
}

@Composable
fun <T> GridSection(
    title: String,
    subtitle: String,
    items: List<T>,
    rows: Int,
    height: Int,
    itemContent: @Composable (T) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        SectionTitle(title = title, subtitle = subtitle)
        LazyHorizontalGrid(
            modifier = Modifier.height(height.dp),
            rows = GridCells.Fixed(rows),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { itemContent(it) }
        }
    }
}

@Composable
fun <T> VerticalGrid(
    modifier: Modifier = Modifier,
    columns: Int,
    items: List<T>,
    itemContent: @Composable (T) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 16.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { itemContent(it) }
    }
}

@Composable
fun <T> VerticalGridDetail(
    modifier: Modifier = Modifier,
    columns: Int,
    items: List<T>,
    itemContent: @Composable (T) -> Unit
) {
    val density = LocalDensity.current
    val navigationBarHeight = WindowInsets.navigationBars.getBottom(density)

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 16.dp + with(density) { navigationBarHeight.toDp() }
        ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { itemContent(it) }
    }
}

@Composable
fun SearchHistorySection(
    searchHistory: List<String>,
    onHistoryItemClick: (String) -> Unit,
    onClearAndHideHistory: () -> Unit,
    onRemoveHistoryItem: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.search_history),
                style = MaterialTheme.typography.titleMedium
            )
            DeleteButton(
                onClick = onClearAndHideHistory,
                text = stringResource(R.string.clear)
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            maxItemsInEachRow = Int.MAX_VALUE
        ) {
            searchHistory.forEach {
                SearchHistoryChip(
                    label = it,
                    onChipClick = { onHistoryItemClick(it) },
                    onRemove = { onRemoveHistoryItem(it) },
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
        }
    }
}

@Composable
fun SuggestionsSection(
    suggestions: List<String>,
    searchQuery: String,
    onSuggestionClick: (String) -> Unit
) {
    LazyColumn {
        items(suggestions) { suggestion ->
            Text(
                text = buildAnnotatedString {
                    val startIndex = suggestion.indexOf(searchQuery, ignoreCase = true)
                    if (startIndex >= 0) {
                        append(suggestion.substring(0, startIndex))
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(suggestion.substring(startIndex, startIndex + searchQuery.length))
                        }
                        append(suggestion.substring(startIndex + searchQuery.length))
                    } else {
                        append(suggestion)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSuggestionClick(suggestion) }
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun SearchResultsSection(
    products: List<Product>,
    sortOption: SearchSortOption,
    onSortClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onSaveToggle: (Product) -> Unit
) {
    if (products.isEmpty()) {
        EmptyScreen(title = stringResource(R.string.no_search_results))
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.total_items, products.size),
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
                            stringResource(sortOption.stringResId),
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
            items(products) { product ->
                SearchProductCard(
                    product = product,
                    onClick = { onProductClick(product.productId) },
                    onSaveToggle = { onSaveToggle(product) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}