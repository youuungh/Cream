package com.ninezero.cream.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ninezero.cream.utils.NumUtils.calculatePriceDiff
import com.ninezero.cream.utils.NumUtils.formatPriceWithCommas
import com.ninezero.cream.utils.NumUtils.formatWithCommas
import com.ninezero.cream.utils.NumUtils.getShippingDate
import com.ninezero.di.R
import com.ninezero.domain.model.Brand
import com.ninezero.domain.model.Product

@Composable
fun ProductInfoHeader(product: Product) {
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
fun ProductInfoContainer(product: Product) {
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

@Composable
fun BenefitInfoContainer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        ContainerTitle(title = "추가 혜택", onMoreClick = { /*todo*/ })
        Column {
            BenefitInfo(
                title = "포인트",
                subtitle = "계좌 간편결제 시 1% 적립"
            )
            Spacer(modifier = Modifier.height(4.dp))
            BenefitInfo(
                title = "결제",
                subtitle = "카드 최대 15만원 상당 혜택"
            )
        }
    }
}

@Composable
fun ShippingInfoContainer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        ContainerTitle(title = "배송 정보")
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            ShippingInfo(
                imageRes = R.drawable.fast_shipping,
                title = "빠른배송",
                price = "5,000원",
                subtitle = "지금 결제 시 ${getShippingDate()} 도착 예정"
            )
            Spacer(modifier = Modifier.height(8.dp))
            ShippingInfo(
                imageRes = R.drawable.normal_shipping,
                title = "일반배송",
                price = "3,000원",
                subtitle = "검수 후 배송, 5-7일 내 도착 예정"
            )
        }
    }
}

@Composable
fun BrandInfoContainer(brand: Brand) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        BrandInfo(brand = brand)
    }
}