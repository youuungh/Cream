@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
package com.ninezero.cream.ui.category

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ninezero.cream.ui.component.CategoryCard
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.VerticalGrid
import com.ninezero.cream.ui.component.skeleton.CategorySkeleton
import com.ninezero.cream.base.collectAsState
import com.ninezero.cream.base.collectEvents
import com.ninezero.cream.ui.component.CreamSurface
import com.ninezero.cream.ui.component.GenericTopAppBar
import com.ninezero.cream.viewmodel.CategoryViewModel
import com.ninezero.di.R

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
                    GenericTopAppBar(
                        title = stringResource(R.string.main_category),
                        onCartClick = onCartClick
                    )
                }
            ) { innerPadding ->
                when (val state = uiState) {
                    is CategoryState.Loading -> CategorySkeleton(
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
                        onRetry = { viewModel.action(CategoryAction.Refresh) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}