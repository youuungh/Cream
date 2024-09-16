package com.ninezero.cream.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink
import com.ninezero.di.R

object AppRoutes {
    const val MAIN = "main"

    const val MAIN_HOME = "main_home"
    const val MAIN_CATEGORY = "main_category"
    const val MAIN_SAVED = "main_saved"
    const val MAIN_MY_PAGE = "main_my_page"
    const val CART = "cart"
    const val SEARCH = "search"

    const val CATEGORY_DETAIL = "category_detail"
    const val PRODUCT_DETAIL = "product_detail"
    const val ORDER_HISTORY = "order_history"

    const val CATEGORY_ID_KEY = "categoryId"
    const val CATEGORY_NAME_KEY = "categoryName"
    const val PRODUCT_ID_KEY = "productId"

    const val DEEP_LINK_SCHEME = "cream://"
}

sealed class MainRoute(
    @StringRes override val title: Int,
    val icon: @Composable () -> Painter,
    override val route: String
) : Destination {
    data object Home : MainRoute(
        R.string.main_home,
        { painterResource(id = R.drawable.ic_store) },
        AppRoutes.MAIN_HOME,
    )

    data object Category : MainRoute(
        R.string.main_category,
        { painterResource(id = R.drawable.ic_category) },
        AppRoutes.MAIN_CATEGORY
    )

    data object Saved : MainRoute(
        R.string.main_saved,
        { painterResource(id = R.drawable.ic_save) },
        AppRoutes.MAIN_SAVED
    )

    data object MyPage : MainRoute(
        R.string.main_my_page,
        { painterResource(id = R.drawable.ic_my_page) },
        AppRoutes.MAIN_MY_PAGE
    )

    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "${AppRoutes.DEEP_LINK_SCHEME}$route" }
    )

    companion object {
        val entries: Array<MainRoute> = arrayOf(Home, Category, Saved, MyPage)

        fun fromRoute(route: String?): MainRoute =
            entries.find { it.route == route } ?: Home

        fun isMainRoute(route: String?): Boolean =
            entries.any { it.route == route }
    }
}

interface Destination {
    val title: Int
    val route: String
    val deepLinks: List<NavDeepLink>
}



