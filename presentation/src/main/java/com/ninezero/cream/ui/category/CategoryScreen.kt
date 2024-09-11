package com.ninezero.cream.ui.category

import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ninezero.cream.ui.component.CategoryCard
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.VerticalGridSection
import com.ninezero.cream.utils.collectAsState
import com.ninezero.cream.utils.collectEvents
import com.ninezero.cream.viewmodel.CategoryViewModel
import com.ninezero.di.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel,
    onCategoryClick: (String) -> Unit,
    onOpenCart: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()

    viewModel.collectEvents {
        when (it) {
            is CategoryEvent.NavigateToCategoryDetail -> onCategoryClick(it.categoryId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    IconButton(
                        onClick = onOpenCart,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_bag), contentDescription = "Cart")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        when (val state = uiState) {
            is CategoryState.Loading -> CategorySkeleton(modifier = Modifier.padding(innerPadding))
            is CategoryState.Content -> VerticalGridSection(
                items = state.categories,
                columns = 2,
                modifier = Modifier.padding(innerPadding)
            ) {
                CategoryCard(
                    category = it,
                    onClick = { viewModel.action(CategoryAction.CategoryClicked(it.categoryId)) }
                )
            }
            is CategoryState.Error -> ErrorScreen(
                onRetry = { viewModel.action(CategoryAction.Refresh) },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}