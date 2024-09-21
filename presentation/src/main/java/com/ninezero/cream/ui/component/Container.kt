package com.ninezero.cream.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ninezero.cream.utils.NumUtils.getShippingDate
import com.ninezero.di.R

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
            .padding(bottom = 16.dp)
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