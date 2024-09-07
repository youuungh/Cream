package com.ninezero.domain.model

data class HomeData(
    val topBanners: List<TopBanner>,
    val banner: Banner?,
    val justDropped: List<Product>,
    val mostPopular: List<Product>,
    val forYou: List<Product>,
    val brands: List<Brand>
)
