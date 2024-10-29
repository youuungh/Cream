@file:OptIn(ExperimentalSharedTransitionApi::class)
package com.ninezero.cream.ui.category

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.ninezero.domain.model.Category

@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    onCartClick: () -> Unit,
    onCategoryClick: (String, String) -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()

    viewModel.collectEvents {
        when (it) {
            is CategoryEvent.NavigateToCategoryDetail -> onCategoryClick(it.categoryId, it.categoryName)
        }
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
                CategoryScreenContent(
                    uiState = uiState,
                    onCategoryClick = { categoryId, categoryName ->
                        viewModel.action(CategoryAction.CategoryClicked(categoryId, categoryName))
                    },
                    onRetry = { viewModel.action(CategoryAction.Fetch) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun CategoryScreenContent(
    uiState: CategoryState,
    onCategoryClick: (String, String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is CategoryState.Fetching -> CategorySkeleton(modifier = modifier)
        is CategoryState.Content -> CategoryGrid(
            categories = uiState.categories,
            onCategoryClick = onCategoryClick,
            modifier = modifier
        )
        is CategoryState.Error -> ErrorScreen(
            onRetry = onRetry,
            modifier = modifier
        )
    }
}

@Composable
private fun CategoryGrid(
    categories: List<Category>,
    onCategoryClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    VerticalGrid(
        columns = 2,
        items = categories,
        modifier = modifier
    ) { category ->
        key(category.categoryId) {
            CategoryCard(
                category = category,
                onCategoryClick = onCategoryClick
            )
        }
    }
}