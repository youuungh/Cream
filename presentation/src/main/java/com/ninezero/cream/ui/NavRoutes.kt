package com.ninezero.cream.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.ninezero.cream.utils.GsonUtils
import com.ninezero.di.R

sealed class MainRoute(
    override val route: String,
    val icon: @Composable () -> Painter,
    override val title: String
) : Destination {
    object Home : MainRoute(
        AppRoutes.MAIN_HOME,
        { painterResource(id = R.drawable.ic_store) },
        RouteTitles.MAIN_HOME
    )

    object Category : MainRoute(
        AppRoutes.MAIN_CATEGORY,
        { painterResource(id = R.drawable.ic_category) },
        RouteTitles.MAIN_CATEGORY
    )

    object Saved : MainRoute(
        AppRoutes.MAIN_SAVED,
        { painterResource(id = R.drawable.ic_save) },
        RouteTitles.MAIN_SAVED
    )

    object MyPage : MainRoute(
        AppRoutes.MAIN_MY_PAGE,
        { painterResource(id = R.drawable.ic_my_page) },
        RouteTitles.MAIN_MY_PAGE
    )

    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "${AppRoutes.DEEP_LINK_SCHEME}$route" }
    )

    companion object {
        fun isMainRoute(route: String?): Boolean {
            return route in listOf(
                AppRoutes.MAIN_HOME,
                AppRoutes.MAIN_CATEGORY,
                AppRoutes.MAIN_SAVED,
                AppRoutes.MAIN_MY_PAGE
            )
        }
    }
}

object CartRoute : Destination {
    override val route: String = AppRoutes.CART
    override val title: String = RouteTitles.CART
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "${AppRoutes.DEEP_LINK_SCHEME}$route" }
    )
}

object SearchRoute : Destination {
    override val route: String = AppRoutes.SEARCH
    override val title: String = RouteTitles.SEARCH
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "${AppRoutes.DEEP_LINK_SCHEME}$route" }
    )
}

object OrderHistoryRoute : Destination {
    override val route: String = AppRoutes.ORDER_HISTORY
    override val title: String = RouteTitles.ORDER_HISTORY
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "${AppRoutes.DEEP_LINK_SCHEME}$route" }
    )
}

object CategoryDetailRoute : DestinationArg<String> {
    override val route: String = AppRoutes.CATEGORY_DETAIL
    override val title: String = RouteTitles.CATEGORY_DETAIL
    override val argName: String = "categoryId"
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "${AppRoutes.DEEP_LINK_SCHEME}$route/{$argName}" }
    )

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(argName) { type = NavType.StringType }
    )

    override fun navigateWithArg(item: String): String {
        return "$route/$item"
    }

    override fun findArgument(navBackStackEntry: NavBackStackEntry): String? {
        return navBackStackEntry.arguments?.getString(argName)
    }
}

object ProductDetailRoute : DestinationArg<String> {
    override val route: String = AppRoutes.PRODUCT_DETAIL
    override val title: String = RouteTitles.PRODUCT_DETAIL
    override val argName: String = "productId"
    override val deepLinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "${AppRoutes.DEEP_LINK_SCHEME}$route/{$argName}" }
    )

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(argName) { type = NavType.StringType }
    )

    override fun navigateWithArg(item: String): String {
        val arg = GsonUtils.toJson(item)
        return "$route/$arg"
    }

    override fun findArgument(navBackStackEntry: NavBackStackEntry): String? {
        val productIdString = navBackStackEntry.arguments?.getString(argName)
        return GsonUtils.fromJson<String>(productIdString)
    }
}

interface Destination {
    val route: String
    val title: String
    val deepLinks: List<NavDeepLink>
}

interface DestinationArg<T> : Destination {
    val argName: String
    val arguments: List<NamedNavArgument>
    fun routeWithArgName() = "$route/{$argName}"
    fun navigateWithArg(item: T): String
    fun findArgument(navBackStackEntry: NavBackStackEntry): T?
}

object AppRoutes {
    const val DEEP_LINK_SCHEME = "cream://"
    const val MAIN_HOME = "main_home"
    const val MAIN_CATEGORY = "main_category"
    const val MAIN_SAVED = "main_saved"
    const val MAIN_MY_PAGE = "main_my_page"
    const val CART = "cart"
    const val SEARCH = "search"
    const val CATEGORY_DETAIL = "category_detail"
    const val PRODUCT_DETAIL = "product_detail"
    const val ORDER_HISTORY = "order_history"
}

object RouteTitles {
    const val MAIN_HOME = "HOME"
    const val MAIN_CATEGORY = "CATEGORY"
    const val MAIN_SAVED = "SAVED"
    const val MAIN_MY_PAGE = "MY PAGE"
    const val CART = "장바구니"
    const val SEARCH = "검색"
    const val CATEGORY_DETAIL = "카테고리 상세"
    const val PRODUCT_DETAIL = "상품 상세"
    const val ORDER_HISTORY = "구매 내역"
}