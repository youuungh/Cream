@file:OptIn(ExperimentalMaterial3Api::class)
package com.ninezero.cream.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ninezero.cream.utils.SavedSortOption
import com.ninezero.cream.utils.SearchSortOption
import com.ninezero.di.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SavedBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    coroutineScope: CoroutineScope,
    selectedOption: SavedSortOption,
    onOptionSelected: (SavedSortOption) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { showBottomSheet.value = false },
        sheetState = sheetState,
        windowInsets = WindowInsets(0.dp),
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    ) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.sort),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal
                    )
                )
                IconButton(onClick = {
                    coroutineScope.launch { sheetState.hide() }
                        .invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet.value = false
                        }
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close"
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        SortOptionCard(
            text = stringResource(id = R.string.sort_by_saved_date),
            selected = selectedOption == SavedSortOption.SAVED_DATE,
            onClick = {
                onOptionSelected(SavedSortOption.SAVED_DATE)
                coroutineScope.launch { sheetState.hide() }
                    .invokeOnCompletion {
                        if (!sheetState.isVisible) showBottomSheet.value = false
                    }
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        SortOptionCard(
            text = stringResource(id = R.string.sort_by_price),
            selected = selectedOption == SavedSortOption.PRICE,
            onClick = {
                onOptionSelected(SavedSortOption.PRICE)
                coroutineScope.launch { sheetState.hide() }
                    .invokeOnCompletion {
                        if (!sheetState.isVisible) showBottomSheet.value = false
                    }
            },
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp + BottomSheetDefaults.windowInsets.asPaddingValues().calculateBottomPadding()
            )
        )
    }
}

@Composable
fun DetailBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    onDismiss: () -> Unit,
    coroutineScope: CoroutineScope,
    productImageUrl: String,
    productName: String,
    productKo: String,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = {
            showBottomSheet.value = false
            onDismiss()
        },
        sheetState = sheetState,
        windowInsets = WindowInsets(0.dp),
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    ) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.bottom_sheet_buy),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.bottom_sheet_buy_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
                IconButton(onClick = {
                    coroutineScope.launch { sheetState.hide() }
                        .invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet.value = false
                        }
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close"
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(productImageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = productName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Normal
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = productKo,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Normal
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = BottomSheetDefaults.windowInsets.asPaddingValues().calculateBottomPadding()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    text = stringResource(id = R.string.bottom_sheet_add_to_cart),
                    onClick = {
                        onAddToCart()
                        coroutineScope.launch {
                            sheetState.hide()
                            showBottomSheet.value = false
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                )
                FilledButton(
                    text = stringResource(id = R.string.bottom_sheet_buy_now),
                    onClick = onBuyNow,
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SortBottomSheet(
    selectedOption: SearchSortOption,
    onOptionSelected: (SearchSortOption) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        windowInsets = WindowInsets(0.dp),
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp + BottomSheetDefaults.windowInsets.asPaddingValues().calculateBottomPadding()
            )
        ) {
            SearchSortOption.entries.forEach { option ->
                SortOptionItem(
                    option = option,
                    isSelected = option == selectedOption,
                    onSelect = { onOptionSelected(option) }
                )
                if (option != SearchSortOption.entries.last()) { Divider() }
            }
        }
    }
}