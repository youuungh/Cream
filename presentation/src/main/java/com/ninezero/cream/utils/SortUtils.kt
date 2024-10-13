package com.ninezero.cream.utils

import androidx.annotation.StringRes
import com.ninezero.di.R

enum class SavedSortOption(@StringRes val stringResId: Int) {
    SAVED_DATE(R.string.sort_by_saved_date),
    PRICE(R.string.sort_by_price)
}

enum class SearchSortOption(@StringRes val stringResId: Int) {
    RECOMMENDED(R.string.sort_recommended),
    PRICE_LOW_TO_HIGH(R.string.sort_price_low_to_high),
    PRICE_HIGH_TO_LOW(R.string.sort_price_high_to_low)
}