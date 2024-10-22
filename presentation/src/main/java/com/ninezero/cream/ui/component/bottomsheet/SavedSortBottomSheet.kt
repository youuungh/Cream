@file:OptIn(ExperimentalMaterial3Api::class)
package com.ninezero.cream.ui.component.bottomsheet

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ninezero.cream.ui.component.BottomSheetLayout
import com.ninezero.cream.ui.component.SortOptionCard
import com.ninezero.cream.utils.SavedSortOption
import com.ninezero.di.R

@Composable
fun SavedSortBottomSheetContent(
    selectedOption: SavedSortOption,
    onOptionSelected: (SavedSortOption) -> Unit,
    onDismiss: () -> Unit
) {
    BottomSheetLayout(
        title = stringResource(id = R.string.sort),
        onDismiss = onDismiss
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        SortOptionCard(
            text = stringResource(id = R.string.sort_by_saved_date),
            selected = selectedOption == SavedSortOption.SAVED_DATE,
            onClick = {
                onOptionSelected(SavedSortOption.SAVED_DATE)
                onDismiss()
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        SortOptionCard(
            text = stringResource(id = R.string.sort_by_price),
            selected = selectedOption == SavedSortOption.PRICE,
            onClick = {
                onOptionSelected(SavedSortOption.PRICE)
                onDismiss()
            },
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp + BottomSheetDefaults.windowInsets.asPaddingValues()
                    .calculateBottomPadding()
            )
        )
    }
}