package com.ninezero.cream.ui.component.skeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ninezero.cream.ui.component.Divider
import com.ninezero.cream.ui.component.ShimmerBox
import com.ninezero.cream.ui.theme.CreamTheme

@Composable
fun SavedSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerBox(
                modifier = Modifier
                    .width(100.dp)
                    .height(24.dp)
            )
            ShimmerBox(
                modifier = Modifier
                    .width(80.dp)
                    .height(24.dp)
            )
        }

        repeat(5) {
            SavedProductCardSkeleton()
            if (it < 4) {
                Divider()
            }
        }
    }
}

@Composable
fun SavedProductCardSkeleton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(vertical = 8.dp)
    ) {
        ShimmerBox(
            modifier = Modifier
                .size(130.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            ShimmerBox(
                modifier = Modifier
                    .width(120.dp)
                    .height(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .width(100.dp)
                        .height(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavedScreenSkeletonPreview() {
    CreamTheme {
        SavedSkeleton()
    }
}