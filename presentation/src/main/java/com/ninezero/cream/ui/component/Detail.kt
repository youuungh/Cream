package com.ninezero.cream.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import coil.compose.AsyncImage
import com.ninezero.cream.ui.product.ProductDetailState
import com.ninezero.cream.utils.CONTENT_OVERLAP
import com.ninezero.cream.utils.DETAIL_BOTTOM_BAR_HEIGHT
import com.ninezero.cream.utils.IMAGE_HEIGHT
import com.ninezero.cream.utils.MAX_CORNER_RADIUS
import com.ninezero.cream.utils.NumUtils
import com.ninezero.cream.utils.NumUtils.calculatePriceDiff
import com.ninezero.cream.utils.NumUtils.formatPriceWithCommas
import com.ninezero.cream.utils.NumUtils.formatWithCommas
import com.ninezero.cream.utils.SCROLL_THRESHOLD_OFFSET
import com.ninezero.domain.model.Product

@Composable
fun ProductDetailContent(
    state: ProductDetailState.Content,
    onNavigateBack: () -> Unit,
    onCartClick: () -> Unit,
    onSaveToggle: () -> Unit,
    onBuyClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val scrollThreshold =
        with(LocalDensity.current) { IMAGE_HEIGHT.dp.toPx() - SCROLL_THRESHOLD_OFFSET.dp.toPx() }

    state.appBarAlpha = (scrollState.value / scrollThreshold).coerceIn(0f, 1f)

    val appBarAlpha by animateFloatAsState(
        targetValue = (scrollState.value / scrollThreshold).coerceIn(0f, 1f),
        label = "appbar_alpha"
    )

    val contentCornerRadius by animateDpAsState(
        targetValue = (1 - (scrollState.value / scrollThreshold).coerceIn(0f, 1f)) * MAX_CORNER_RADIUS.dp,
        label = "content_radius"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            ProductDetailImage(
                imageUrl = state.product.imageUrl,
                height = IMAGE_HEIGHT.dp
            )
            CreamSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = -CONTENT_OVERLAP.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = contentCornerRadius,
                            topEnd = contentCornerRadius
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = DETAIL_BOTTOM_BAR_HEIGHT.dp)
                ) {
                    ProductDetailHeader(product = state.product)
                    ProductDetailInfoCard(product = state.product)
                    Divider()
                    BenefitInfoContainer()
                    Divider()
                    ShippingInfoContainer()
                }
            }
        }

        DetailsAppBar(
            title = "",
            onBackClick = onNavigateBack,
            onCartClick = onCartClick,
            alpha = appBarAlpha,
            modifier = Modifier.fillMaxWidth()
        )

        ProductBottomBar(
            price = formatPriceWithCommas(state.product.price.instantBuyPrice),
            isSaved = state.product.isSaved,
            onSaveToggle = onSaveToggle,
            onBuyClick = onBuyClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ProductDetailHeader(product: Product) {
    Text(
        text = "즉시구매가",
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Normal
        )
    )
    Text(
        text = formatPriceWithCommas(product.price.instantBuyPrice),
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold
        )
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = product.productName,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Normal
        )
    )
    Text(
        text = product.ko,
        style = MaterialTheme.typography.titleSmall.copy(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
            fontWeight = FontWeight.Normal
        )
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun ProductDetailImage(
    imageUrl: String,
    height: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.9f to Color.Black.copy(alpha = 0.1f),
                        1f to Color.Black.copy(alpha = 0.2f)
                    )
                )
        )
    }
}

@Composable
fun ProductDetailInfoCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            ProductInfo(
                title = "발매가",
                value = formatPriceWithCommas(product.price.releasePrice),
                diffInfo = calculatePriceDiff(product.price)
            )
            VerticalDivider(
                modifier = Modifier
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )
            ProductInfo(
                title = "거래량",
                value = formatWithCommas(product.tradingVolume)
            )
            VerticalDivider(
                modifier = Modifier
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )
            ProductInfo(
                title = "출시일",
                value = product.releaseDate
            )
            VerticalDivider(
                modifier = Modifier
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )
            ProductInfo(
                title = "대표색상",
                value = product.mainColor
            )
        }
    }
}


