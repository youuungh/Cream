package com.ninezero.cream.ui.category

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.ProductCard
import com.ninezero.cream.ui.component.VerticalGridSectionWithName
import com.ninezero.cream.utils.collectEvents
import com.ninezero.cream.viewmodel.CategoryDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    viewModel: CategoryDetailViewModel,
    onProductClick: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()

    viewModel.collectEvents {
        // onProductClick
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Category Detail",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
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
            is CategoryDetailState.Loading -> CategoryDetailSkeleton(modifier = Modifier.padding(innerPadding))
            is CategoryDetailState.Content -> {
                VerticalGridSectionWithName(
                    name = state.categoryDetails.category.ko,
                    items = state.categoryDetails.products,
                    columns = 2,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    ProductCard(
                        product = it,
                        onClick = { onProductClick(it.productId) },
                        onSaveClick = { /* 저장 기능 */ }
                    )
                }
            }
            is CategoryDetailState.Error -> ErrorScreen(
                onRetry = { viewModel.action(CategoryDetailAction.Fetch(state.categoryId)) },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}