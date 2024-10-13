package com.ninezero.cream.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SearchHistoryChip(
    label: String,
    onChipClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    SuggestionChip(
        onClick = onChipClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.defaultMinSize(minWidth = 0.dp)
            ) {
                Text(
                    text = label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Remove",
                    modifier = Modifier
                        .size(18.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onRemove
                        ),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        modifier = modifier,
        border = SuggestionChipDefaults.suggestionChipBorder(
            enabled = true,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(50),
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}