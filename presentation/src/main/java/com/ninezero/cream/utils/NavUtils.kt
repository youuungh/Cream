package com.ninezero.cream.utils

import androidx.navigation.NavHostController
import com.ninezero.cream.ui.AppRoutes
import com.ninezero.cream.ui.CartRoute
import com.ninezero.cream.ui.CategoryDetailRoute
import com.ninezero.cream.ui.Destination
import com.ninezero.cream.ui.MainRoute
import com.ninezero.cream.ui.OrderHistoryRoute
import com.ninezero.cream.ui.ProductDetailRoute
import com.ninezero.cream.ui.SearchRoute

object NavUtils {

    fun navigateTo(
        controller: NavHostController,
        destination: String,
        popUpToRoute: String? = null,
        isSingleTop: Boolean = true,
        restoreState: Boolean = true
    ) {
        controller.navigate(destination) {
            popUpToRoute?.let {
                popUpTo(popUpToRoute) { saveState = true }
            }
            launchSingleTop = isSingleTop
            this.restoreState = restoreState
        }
    }

    fun getDestinationFromRoute(route: String?): Destination {
        return when(route) {
            AppRoutes.MAIN_HOME -> MainRoute.Home
            AppRoutes.MAIN_CATEGORY -> MainRoute.Category
            AppRoutes.MAIN_SAVED -> MainRoute.Saved
            AppRoutes.MAIN_MY_PAGE -> MainRoute.MyPage
            AppRoutes.CART -> CartRoute
            AppRoutes.SEARCH -> SearchRoute
            AppRoutes.ORDER_HISTORY -> OrderHistoryRoute
            CategoryDetailRoute.routeWithArgName() -> CategoryDetailRoute
            ProductDetailRoute.routeWithArgName() -> ProductDetailRoute
            else -> MainRoute.Home
        }
    }
}