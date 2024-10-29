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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.ninezero.cream.ui.component.bottomsheet.AnimatedCreamBottomSheet
import com.ninezero.cream.ui.component.bottomsheet.BottomSheetState
import com.ninezero.cream.ui.component.bottomsheet.PaymentStatus
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
    var showPaymentBottomSheet by remember { mutableStateOf(false) }
    var paymentStatus by remember { mutableStateOf(PaymentStatus.NONE) }

    viewModel.collectEvents {
        when (it) {
            is CartEvent.NavigateToProductDetail -> onProductClick(it.productId)
            is CartEvent.NavigateToHome -> onNavigateToHome()
            CartEvent.PaymentCompleted -> paymentStatus = PaymentStatus.SUCCESS
            CartEvent.PaymentFailed -> paymentStatus = PaymentStatus.FAILED
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
        CartScreenContent(
            uiState = uiState,
            allSelected = allSelected,
            onNavigateToHome = onNavigateToHome,
            onProductClick = onProductClick,
            onRetry = { viewModel.action(CartAction.Fetch) },
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
            onDeleteSelected = { count ->
                selectedCount = count
                showDeleteSelectedDialog = true
            },
            calculateTotalPrice = viewModel::calculateTotalPrice,
            calculateTotalFee = viewModel::calculateTotalFee,
            onOrderClick = { showPaymentBottomSheet = true },
            modifier = Modifier.padding(innerPadding)
        )
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

    if (showPaymentBottomSheet && uiState is CartState.Content) {
        val selectedProducts = (uiState as CartState.Content).products.filter { it.isSelected }

        AnimatedCreamBottomSheet(
            showBottomSheet = remember { mutableStateOf(true) },
            state = when (paymentStatus) {
                PaymentStatus.NONE -> BottomSheetState.Payment(
                    products = selectedProducts,
                    onPaymentClick = {
                        paymentStatus = PaymentStatus.PROCESSING
                        viewModel.action(CartAction.ProcessPayment(selectedProducts))
                    }
                )
                else -> BottomSheetState.PaymentProgress(
                    status = paymentStatus,
                    onNavigateToHome = {
                        showPaymentBottomSheet = false
                        onNavigateToHome()
                    }
                )
            },
            onDismiss = {
                if (paymentStatus != PaymentStatus.PROCESSING) {
                    showPaymentBottomSheet = false
                    paymentStatus = PaymentStatus.NONE
                }
            },
            coroutineScope = rememberCoroutineScope()
        )
    }
}

@Composable
private fun CartScreenContent(
    uiState: CartState,
    allSelected: Boolean,
    onNavigateToHome: () -> Unit,
    onProductClick: (String) -> Unit,
    onRetry: () -> Unit,
    onRemoveProduct: (Product) -> Unit,
    onUpdateSelection: (String, Boolean) -> Unit,
    onUpdateAllSelection: (Boolean) -> Unit,
    onDeleteSelected: (Int) -> Unit,
    calculateTotalPrice: () -> Int,
    calculateTotalFee: () -> Double,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is CartState.Fetching -> CartSkeleton(modifier = modifier)
        is CartState.Content -> {
            if (uiState.products.isEmpty()) {
                EmptyScreen(
                    onNavigateToHome = onNavigateToHome,
                    title = stringResource(R.string.no_cart_items_title),
                    subtitle = stringResource(R.string.no_cart_items_subtitle),
                    label = stringResource(R.string.view_home),
                    modifier = modifier
                )
            } else {
                CartContent(
                    products = uiState.products,
                    allSelected = allSelected,
                    onProductClick = onProductClick,
                    onRemoveProduct = onRemoveProduct,
                    onUpdateSelection = onUpdateSelection,
                    onUpdateAllSelection = onUpdateAllSelection,
                    onDeleteSelected = {
                        onDeleteSelected(uiState.products.count { it.isSelected })
                    },
                    calculateTotalPrice = calculateTotalPrice,
                    calculateTotalFee = calculateTotalFee,
                    onOrderClick = onOrderClick,
                    modifier = modifier
                )
            }
        }
        is CartState.Error -> ErrorScreen(onRetry = onRetry, modifier = modifier)
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
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        CartHeader(
            allSelected = allSelected,
            onUpdateAllSelection = onUpdateAllSelection,
            onDeleteSelected = onDeleteSelected
        )

        CartList(
            products = products,
            onProductClick = onProductClick,
            onRemoveProduct = onRemoveProduct,
            onUpdateSelection = onUpdateSelection,
            calculateTotalPrice = calculateTotalPrice,
            calculateTotalFee = calculateTotalFee,
            modifier = Modifier.weight(1f)
        )

        CartFooter(
            selectedCount = products.count { it.isSelected },
            totalAmount = calculateTotalPrice() + calculateTotalFee().toInt(),
            onOrderClick = onOrderClick
        )
    }
}

@Composable
private fun CartHeader(
    allSelected: Boolean,
    onUpdateAllSelection: (Boolean) -> Unit,
    onDeleteSelected: () -> Unit
) {
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
                    onCheckedChange = onUpdateAllSelection,
                    label = stringResource(R.string.select_all)
                )
            }
            DeleteButton(
                onClick = onDeleteSelected,
                text = stringResource(R.string.delete_selected)
            )
        }
    }
}

@Composable
private fun CartList(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    onRemoveProduct: (Product) -> Unit,
    onUpdateSelection: (String, Boolean) -> Unit,
    calculateTotalPrice: () -> Int,
    calculateTotalFee: () -> Double,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = products,
            key = { it.productId }
        ) { product ->
            CartCard(
                product = product,
                onProductClick = onProductClick,
                onRemoveProduct = onRemoveProduct,
                onUpdateSelection = onUpdateSelection
            )
        }

        item(key = "summary") {
            CartSummaryCard(
                totalPrice = calculateTotalPrice(),
                totalFee = calculateTotalFee()
            )
        }
    }
}

@Composable
private fun CartFooter(
    selectedCount: Int,
    totalAmount: Int,
    onOrderClick: () -> Unit
) {
    CartBottomBar(
        selectedCount = selectedCount,
        totalPrice = totalAmount,
        onOrderClick = onOrderClick
    )
}