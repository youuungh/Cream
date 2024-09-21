package com.ninezero.cream.ui.component.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ninezero.cream.ui.component.CreamSurface
import com.ninezero.cream.ui.component.Divider
import com.ninezero.cream.ui.component.ShimmerBox
import com.ninezero.cream.utils.CONTENT_OVERLAP
import com.ninezero.cream.utils.IMAGE_HEIGHT

@Composable
fun ProductDetailSkeleton(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IMAGE_HEIGHT.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            CreamSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = -CONTENT_OVERLAP.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    repeat(3) {
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .padding(vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            repeat(4) {
                                Column {
                                    ShimmerBox(
                                        modifier = Modifier
                                            .width(60.dp)
                                            .height(16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    ShimmerBox(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Divider()

                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .padding(vertical = 8.dp)
                    )
                    repeat(2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            ShimmerBox(
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(16.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            ShimmerBox(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(16.dp)
                            )
                        }
                    }

                    Divider()

                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .padding(vertical = 8.dp)
                    )
                    repeat(2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            ShimmerBox(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                ShimmerBox(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                ShimmerBox(
                                    modifier = Modifier
                                        .width(180.dp)
                                        .height(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}