package com.ninezero.cream.ui.component.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ninezero.cream.ui.component.FilledButton
import com.ninezero.di.R

@Composable
fun PaymentLottieAnimation(
    modifier: Modifier = Modifier,
    status: PaymentStatus
) {
    val clipSpec = when (status) {
        PaymentStatus.PROCESSING -> LottieClipSpec.Progress(0.0f, 0.28f)
        PaymentStatus.SUCCESS -> LottieClipSpec.Progress(0.3f, 0.45f)
        PaymentStatus.FAILED -> LottieClipSpec.Progress(0.8f, 0.95f)
        else -> null
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.anim_loading_success_failed)
    )

    LottieAnimation(
        modifier = modifier,
        composition = composition,
        iterations = when (status) {
            PaymentStatus.PROCESSING -> LottieConstants.IterateForever
            else -> 1
        },
        clipSpec = clipSpec
    )
}

@Composable
fun PaymentProgressContent(
    status: PaymentStatus,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            when (status) {
                PaymentStatus.PROCESSING -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        PaymentLottieAnimation(
                            status = status,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.payment_processing),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                PaymentStatus.SUCCESS, PaymentStatus.FAILED -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        PaymentLottieAnimation(
                            status = status,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(
                                if (status == PaymentStatus.SUCCESS) R.string.payment_success
                                else R.string.payment_failed
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {}
            }
        }

        if (status == PaymentStatus.SUCCESS || status == PaymentStatus.FAILED) {
            FilledButton(
                text = stringResource(R.string.go_to_home),
                onClick = onNavigateToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
        }
    }
}