@file:OptIn(ExperimentalMaterial3Api::class)
package com.ninezero.cream.ui.component.bottomsheet

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BaseBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    onDismiss: () -> Unit,
    coroutineScope: CoroutineScope,
    skipPartiallyExpanded: Boolean = true,
    content: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch { sheetState.hide() }
                .invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet.value = false
                        onDismiss()
                    }
                }
        },
        sheetState = sheetState,
        windowInsets = WindowInsets(0.dp),
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    ) {
        content()
    }
}