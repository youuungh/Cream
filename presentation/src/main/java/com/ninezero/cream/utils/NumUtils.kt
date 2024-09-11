package com.ninezero.cream.utils

import android.annotation.SuppressLint
import java.text.NumberFormat
import java.util.Locale

object NumUtils {
    fun formatWithCommas(number: Int): String {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
    }

    @SuppressLint("DefaultLocale")
    fun formatTradingVolume(volume: Int): String {
        return when {
            volume >= 10000 -> String.format("%.1f만", volume / 10000.0)
            else -> formatWithCommas(volume)
        }
    }
}