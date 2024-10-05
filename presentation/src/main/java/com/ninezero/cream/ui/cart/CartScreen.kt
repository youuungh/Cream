@file:OptIn(ExperimentalMaterial3Api::class)
package com.ninezero.cream.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ninezero.cream.base.collectAsState
import com.ninezero.cream.base.collectEvents
import com.ninezero.cream.ui.component.CartBottomBar
import com.ninezero.cream.ui.component.CartCard
import com.ninezero.cream.ui.component.CartSummaryCard
import com.ninezero.cream.ui.component.CreamScaffold
import com.ninezero.cream.ui.component.CreamSurface
import com.ninezero.cream.ui.component.CustomCheckbox
import com.ninezero.cream.ui.component.CustomDialog
import com.ninezero.cream.ui.component.DeleteButton
import com.ninezero.cream.ui.component.EmptyScreen
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.skeleton.CartSkeleton
import com.ninezero.cream.viewmodel.CartViewModel
import com.ninezero.di.R
import com.ninezero.domain.model.Product

@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val allSelected by viewModel.allSelected.collectAsState()
    var showDeleteSelectedDialog by remember { mutableStateOf(false) }
    var showDeleteSingleItemDialog by remember { mutableStateOf(false) }
    var selectedCount by remember { mutableIntStateOf(0) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    viewModel.collectEvents {
        when (it) {
            is CartEvent.NavigateToProductDetail -> onProductClick(it.productId)
        }
    }

    CreamScaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.cart)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is CartState.Fetching -> CartSkeleton(modifier = Modifier.padding(innerPadding))

            is CartState.Content -> {
                if (state.products.isEmpty()) {
                    EmptyScreen(
                        onNavigateToHome = onNavigateToHome,
                        title = stringResource(R.string.no_cart_items_title),
                        subtitle = stringResource(R.string.no_cart_items_subtitle),
                        label = stringResource(R.string.view_home),
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    CartContent(
                        products = state.products,
                        allSelected = allSelected,
                        onProductClick = onProductClick,
                        onRemoveProduct = { product ->
                            productToDelete = product
                            showDeleteSingleItemDialog = true
                        },
                        onUpdateSelection = { productId, isSelected ->
                            viewModel.action(CartAction.UpdateSelection(productId, isSelected))
                        },
                        onUpdateAllSelection = { isSelected ->
                            viewModel.action(CartAction.UpdateAllSelection(isSelected))
                        },
                        onDeleteSelected = {
                            selectedCount = state.products.count { it.isSelected }
                            showDeleteSelectedDialog = true
                        },
                        calculateTotalPrice = viewModel::calculateTotalPrice,
                        calculateTotalFee = viewModel::calculateTotalFee,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }

            is CartState.Error -> ErrorScreen(
                onRetry = { viewModel.action(CartAction.Fetch) },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    if (showDeleteSelectedDialog) {
        if (selectedCount > 0) {
            CustomDialog(
                onDismissRequest = { showDeleteSelectedDialog = false },
                title = stringResource(R.string.dialog_delete_selected_items),
                content = stringResource(R.string.dialog_delete_selected_items_confirm, selectedCount),
                confirmButtonText = stringResource(R.string.delete),
                onConfirmClick = {
                    viewModel.action(CartAction.RemoveSelected)
                    showDeleteSelectedDialog = false
                }
            )
        } else {
            CustomDialog(
                onDismissRequest = { showDeleteSelectedDialog = false },
                title = stringResource(R.string.dialog_delete_selected_items),
                content = stringResource(R.string.dialog_no_items_selected),
                confirmButtonText = stringResource(R.string.confirm),
                onConfirmClick = { showDeleteSelectedDialog = false },
                showDismissButton = false
            )
        }
    }

    if (showDeleteSingleItemDialog) {
        productToDelete?.let { product ->
            CustomDialog(
                onDismissRequest = {
                    showDeleteSingleItemDialog = false
                    productToDelete = null
                },
                title = stringResource(R.string.dialog_delete_item),
                content = stringResource(R.string.dialog_delete_item_confirm),
                confirmButtonText = stringResource(R.string.delete),
                onConfirmClick = {
                    viewModel.action(CartAction.Remove(product))
                    showDeleteSingleItemDialog = false
                    productToDelete = null
                }
            )
        }
    }
}

@Composable
fun CartContent(
    products: List<Product>,
    allSelected: Boolean,
    onProductClick: (String) -> Unit,
    onRemoveProduct: (Product) -> Unit,
    onUpdateSelection: (String, Boolean) -> Unit,
    onUpdateAllSelection: (Boolean) -> Unit,
    onDeleteSelected: () -> Unit,
    calculateTotalPrice: () -> Int,
    calculateTotalFee: () -> Double,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        CreamSurface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    CustomCheckbox(
                        checked = allSelected,
                        onCheckedChange = { isChecked -> onUpdateAllSelection(isChecked) },
                        label = stringResource(R.string.select_all),
                    )
                }
                DeleteButton(
                    onClick = onDeleteSelected,
                    text = stringResource(R.string.delete_selected)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                CartCard(
                    product = product,
                    onProductClick = onProductClick,
                    onRemoveProduct = onRemoveProduct,
                    onUpdateSelection = onUpdateSelection
                )
            }

            item {
                CartSummaryCard(
                    totalPrice = calculateTotalPrice(),
                    totalFee = calculateTotalFee()
                )
            }
        }

        val totalAmount = calculateTotalPrice() + calculateTotalFee().toInt()
        CartBottomBar(
            selectedCount = products.count { it.isSelected },
            totalPrice = totalAmount,
            onOrderClick = { /*todo*/ }
        )
    }
}