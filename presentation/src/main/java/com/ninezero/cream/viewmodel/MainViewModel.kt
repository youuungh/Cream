package com.ninezero.cream.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.ninezero.cream.ui.CartRoute
import com.ninezero.cream.ui.SearchRoute
import com.ninezero.cream.utils.NavUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    fun openSearch(navHostController: NavHostController) {
        NavUtils.navigateTo(navHostController, SearchRoute.route)
    }

    fun openCart(navHostController: NavHostController) {
        NavUtils.navigateTo(navHostController, CartRoute.route)
    }
}