@file:OptIn(ExperimentalMaterial3Api::class)
package com.ninezero.cream.ui.component.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ninezero.cream.ui.component.BottomSheetLayout
import com.ninezero.cream.ui.component.FilledButton
import com.ninezero.cream.ui.component.PaymentInfo
import com.ninezero.cream.ui.component.PaymentProductCard
import com.ninezero.cream.utils.NumUtils.calculateTotalFee
import com.ninezero.cream.utils.NumUtils.calculateTotalPayment
import com.ninezero.cream.utils.NumUtils.calculateTotalPrice
import com.ninezero.di.R
import com.ninezero.domain.model.Product

@Composable
fun PaymentBottomSheetContent(
    onDismiss: () -> Unit,
    products: List<Product>,
    onPaymentClick: () -> Unit
) {
    BottomSheetLayout(
        title = stringResource(id = R.string.bottom_sheet_payment),
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column {
                    products.forEach { product ->
                        PaymentProductCard(product)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            val totalPrice = calculateTotalPrice(products)
            val totalFee = calculateTotalFee(totalPrice)
            val totalPayment = calculateTotalPayment(totalPrice, totalFee)

            PaymentInfo(
                totalPrice = totalPrice,
                totalFee = totalFee,
                totalPayment = totalPayment
            )
            Spacer(modifier = Modifier.height(24.dp))

            FilledButton(
                text = stringResource(R.string.bottom_sheet_proceed_to_payment),
                onClick = onPaymentClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )

            Spacer(
                modifier = Modifier.padding(
                    bottom = BottomSheetDefaults.windowInsets
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
            )
        }
    }
}