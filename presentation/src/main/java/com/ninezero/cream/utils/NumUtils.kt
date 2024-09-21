package com.ninezero.cream.utils

import android.annotation.SuppressLint
import com.ninezero.domain.model.Price
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.absoluteValue

data class PriceDiffInfo(
    val diffAmount: String,
    val diffPercent: String,
    val isIncrease: Boolean
)

object NumUtils {
    fun formatPriceWithCommas(number: Int?): String {
        return number?.let { NumberFormat.getNumberInstance(Locale.getDefault()).format(number) + "원" } ?: "-"
    }

    fun formatWithCommas(number: Int?): String {
        return number?.let { NumberFormat.getNumberInstance(Locale.getDefault()).format(number) } ?: "-"
    }

    @SuppressLint("DefaultLocale")
    fun formatTradingVolume(volume: Int): String {
        return when {
            volume >= 10000 -> String.format("%.1f만", volume / 10000.0)
            else -> formatWithCommas(volume)
        }
    }

    fun calculatePriceDiff(price: Price): PriceDiffInfo? {
        val release = price.releasePrice ?: return null
        val instant = price.instantBuyPrice
        val diffAmount = instant - release
        val diffPercent = (diffAmount.toFloat() / release.toFloat() * 100).toInt()
        val isIncrease = diffAmount > 0
        val sign = if (isIncrease) "+" else "-"

        return PriceDiffInfo(
            diffAmount = formatPriceWithCommas(diffAmount.absoluteValue),
            diffPercent = "$sign${diffPercent.absoluteValue}%",
            isIncrease = isIncrease
        )
    }

    fun getShippingDate(): String {
        val today = LocalDate.now()
        val nextShippingDate = when (today.dayOfWeek.value) {
            6 -> today.plusDays(2)
            7 -> today.plusDays(1)
            else -> today.plusDays(1)
        }

        val formatter = DateTimeFormatter.ofPattern("M/d(E)")
        return "내일 ${nextShippingDate.format(formatter)}"
    }
}