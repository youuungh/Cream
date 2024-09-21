package com.ninezero.cream.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ninezero.domain.model.Product

@Composable
fun RowSection(
    title: String,
    subtitle: String,
    products: List<Product>,
    onProductClick: (String) -> Unit,
    onSaveClick: (String) -> Unit
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
                    onSaveClick = { /*TODO*/ }
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