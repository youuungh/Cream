package com.ninezero.domain.model

data class Price(
    val releasePrice: Int,
    val instantBuyPrice: Int,
    val status: PriceStatus
)
