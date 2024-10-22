@file:OptIn(ExperimentalMaterial3Api::class)
package com.ninezero.cream.ui.component.bottomsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ninezero.cream.ui.component.BottomSheetLayout
import com.ninezero.cream.ui.component.FilledButton
import com.ninezero.cream.ui.component.OutlinedButton
import com.ninezero.di.R

@Composable
fun DetailBottomSheetContent(
    onDismiss: () -> Unit,
    productImageUrl: String,
    productName: String,
    productKo: String,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    BottomSheetLayout(
        title = stringResource(id = R.string.bottom_sheet_buy),
        subtitle = stringResource(id = R.string.bottom_sheet_buy_label),
        onDismiss = onDismiss
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .padding(horizontal = 16.dp)
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
                .padding(
                    bottom = 16.dp + BottomSheetDefaults.windowInsets
                        .asPaddingValues()
                        .calculateBottomPadding()
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                text = stringResource(id = R.string.bottom_sheet_add_to_cart),
                onClick = onAddToCart,
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
    }
}