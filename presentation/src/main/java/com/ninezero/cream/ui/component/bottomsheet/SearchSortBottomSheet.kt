@file:OptIn(ExperimentalMaterial3Api::class)
package com.ninezero.cream.ui.component.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ninezero.cream.ui.component.BottomSheetLayout
import com.ninezero.cream.ui.component.Divider
import com.ninezero.cream.ui.component.SortOptionItem
import com.ninezero.cream.utils.SearchSortOption

@Composable
fun SearchSortBottomSheetContent(
    selectedOption: SearchSortOption,
    onOptionSelected: (SearchSortOption) -> Unit,
    onDismiss: () -> Unit
) {
    BottomSheetLayout(
        showCloseButton = false,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier.padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp + BottomSheetDefaults.windowInsets.asPaddingValues()
                    .calculateBottomPadding()
            )
        ) {
            SearchSortOption.entries.forEach { option ->
                SortOptionItem(
                    option = option,
                    isSelected = option == selectedOption,
                    onSelect = { onOptionSelected(option) }
                )
                if (option != SearchSortOption.entries.last()) {
                    Divider()
                }
            }
        }
    }
}