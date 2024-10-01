@file:OptIn(ExperimentalSharedTransitionApi::class)
package com.ninezero.cream.ui.category

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ninezero.cream.ui.component.CategoryCard
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.VerticalGrid
import com.ninezero.cream.ui.component.skeleton.CategorySkeleton
import com.ninezero.cream.base.collectAsState
import com.ninezero.cream.base.collectEvents
import com.ninezero.cream.ui.component.CreamSurface
import com.ninezero.cream.ui.component.CreamTopAppBar
import com.ninezero.cream.viewmodel.CategoryViewModel
import com.ninezero.di.R
import timber.log.Timber

@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    onCartClick: () -> Unit,
    onCategoryClick: (String, String) -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val networkState by viewModel.networkState.collectAsState()

    viewModel.collectEvents {
        when (it) {
            is CategoryEvent.NavigateToCategoryDetail -> onCategoryClick(it.categoryId, it.categoryName)
        }
    }

    LaunchedEffect(networkState) {
        Timber.d("networkState: $networkState")
    }

    CreamSurface(modifier = modifier.fillMaxSize()) {
        SharedTransitionLayout {
            Scaffold(
                topBar = {
                    CreamTopAppBar(
                        title = stringResource(R.string.main_category),
                        onCartClick = onCartClick
                    )
                }
            ) { innerPadding ->
                when (val state = uiState) {
                    is CategoryState.Fetching -> CategorySkeleton(
                        modifier = Modifier.padding(innerPadding)
                    )

                    is CategoryState.Content -> {
                        key(state.categories) {
                            VerticalGrid(
                                columns = 2,
                                items = state.categories,
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                CategoryCard(
                                    category = it,
                                    onCategoryClick = { categoryId, categoryName ->
                                        viewModel.action(CategoryAction.CategoryClicked(categoryId, categoryName))
                                    }
                                )
                            }
                        }
                    }

                    is CategoryState.Error -> ErrorScreen(
                        onRetry = { viewModel.action(CategoryAction.Fetch) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}