package com.ninezero.cream.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink
import com.ninezero.di.R

object Routes {
    const val MAIN = "main"

    const val MAIN_HOME = "main_home"
    const val MAIN_CATEGORY = "main_category"
    const val MAIN_SAVED = "main_saved"
    const val MAIN_MY_PAGE = "main_my_page"

    const val CART = "cart"
    const val SEARCH = "search"

    const val PRODUCT_DETAIL = "product_detail"
    const val CATEGORY_DETAIL = "category_detail"
    const val ORDER_HISTORY = "order_history"

    const val PRODUCT_ID_KEY = "productId"
    const val CATEGORY_ID_KEY = "categoryId"
    const val CATEGORY_NAME_KEY = "categoryName"

    const val DEEP_LINK_SCHEME = "cream://"

    fun createDeepLink(route: String) = "$DEEP_LINK_SCHEME$route"

    fun productDetailRoute(productId: String) = "$PRODUCT_DETAIL/$productId"
    fun categoryDetailRoute(categoryId: String, categoryName: String) = "$CATEGORY_DETAIL/$categoryId/$categoryName"
}

sealed class Destination(
    @StringRes val title: Int,
    val route: String
) {
    val deepLink: NavDeepLink = navDeepLink { uriPattern = Routes.createDeepLink(route) }
}

sealed class MainRoute(
    @StringRes title: Int,
    @DrawableRes val iconResId: Int,
    route: String
) : Destination(title, route) {
    @Composable
    fun icon() = painterResource(id = iconResId)

    object Home : MainRoute(R.string.main_home, R.drawable.ic_store, Routes.MAIN_HOME,)
    object Category : MainRoute(R.string.main_category, R.drawable.ic_category, Routes.MAIN_CATEGORY)
    object Saved : MainRoute(R.string.main_saved, R.drawable.ic_save, Routes.MAIN_SAVED)
    object MyPage : MainRoute(R.string.main_my_page, R.drawable.ic_my_page, Routes.MAIN_MY_PAGE)

    companion object {
        val entries = listOf(Home, Category, Saved, MyPage)
        fun fromRoute(route: String?) = entries.find { it.route == route } ?: Home
        fun isMainRoute(route: String?) = entries.any { it.route == route }
    }
}