@file:OptIn(ExperimentalFoundationApi::class)
package com.ninezero.cream.ui.product_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ninezero.cream.base.collectAsState
import com.ninezero.cream.base.collectEvents
import com.ninezero.cream.ui.component.BenefitInfoContainer
import com.ninezero.cream.ui.component.BrandInfoContainer
import com.ninezero.cream.ui.component.ColorSpacer
import com.ninezero.cream.ui.component.CreamSurface
import com.ninezero.cream.ui.component.CustomSnackbar
import com.ninezero.cream.ui.component.Divider
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.ProductBottomBar
import com.ninezero.cream.ui.component.ProductDetailAppBar
import com.ninezero.cream.ui.component.ProductDetailImage
import com.ninezero.cream.ui.component.ProductInfoContainer
import com.ninezero.cream.ui.component.ProductInfoHeader
import com.ninezero.cream.ui.component.RecommendInfo
import com.ninezero.cream.ui.component.ShippingInfoContainer
import com.ninezero.cream.ui.component.StyleInfo
import com.ninezero.cream.ui.component.TabSection
import com.ninezero.cream.ui.component.bottomsheet.AnimatedCreamBottomSheet
import com.ninezero.cream.ui.component.bottomsheet.BottomSheetState
import com.ninezero.cream.ui.component.bottomsheet.BottomSheetType
import com.ninezero.cream.ui.component.bottomsheet.PaymentStatus
import com.ninezero.cream.ui.component.getVisibilityModifier
import com.ninezero.cream.ui.component.rememberAppBarAlphaState
import com.ninezero.cream.ui.component.rememberContentCornerRadiusState
import com.ninezero.cream.ui.component.rememberCreamScaffoldState
import com.ninezero.cream.ui.component.skeleton.ProductDetailSkeleton
import com.ninezero.cream.utils.BOTTOM_BAR_HEIGHT
import com.ninezero.cream.utils.CONTENT_OVERLAP
import com.ninezero.cream.utils.NumUtils.formatPriceWithCommas
import com.ninezero.cream.utils.ProductDetailTab
import com.ninezero.cream.utils.TAB_KEY
import com.ninezero.cream.utils.rememberSlideInOutAnimState
import com.ninezero.cream.viewmodel.ProductDetailViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductDetailScreen(
    onCartClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSaved: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val scaffoldState = rememberCreamScaffoldState()

    var visible by remember { mutableStateOf(false) }
    var appBarAlpha by remember { mutableFloatStateOf(0f) }
    var appBarHeight by remember { mutableStateOf(0.dp) }
    var tabVisible by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var bottomSheetType by remember { mutableStateOf(BottomSheetType.NONE) }
    var paymentStatus by remember { mutableStateOf(PaymentStatus.NONE) }

    val animState = rememberSlideInOutAnimState()

    viewModel.collectEvents {
        when (it) {
            is ProductDetailEvent.NavigateToLogin -> onNavigateToLogin()
            is ProductDetailEvent.NavigateToSaved -> onNavigateToSaved()
            is ProductDetailEvent.NavigateToCart -> onCartClick()
            is ProductDetailEvent.NavigateToHome -> onNavigateToHome()
            is ProductDetailEvent.UpdateBottomSheet -> {
                showBottomSheet = it.visible
                bottomSheetType = it.type
                paymentStatus = it.status
            }
            is ProductDetailEvent.ShowSnackbar -> scaffoldState.showSnackbar(it.message)
            ProductDetailEvent.PaymentCompleted -> paymentStatus = PaymentStatus.SUCCESS
            ProductDetailEvent.PaymentFailed -> paymentStatus = PaymentStatus.FAILED
        }
    }

    LaunchedEffect(Unit) { visible = true }

    LaunchedEffect(uiState) {
        if (uiState is ProductDetailState.Content) {
            tabVisible = true
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = animState.enterTransition,
        exit = animState.exitTransition
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is ProductDetailState.Fetching -> ProductDetailSkeleton()

                is ProductDetailState.Content -> {
                    val detailUIState = ProductDetailUIState(
                        product = state.product,
                        relatedProducts = state.relatedProducts,
                        isSaved = state.product.isSaved,
                        appBarHeight = appBarHeight,
                        tabVisible = tabVisible
                    )

                    val bottomSheetUIState = BottomSheetUIState(
                        isVisible = showBottomSheet,
                        type = bottomSheetType,
                        paymentStatus = paymentStatus
                    )

                    val handlers = ProductDetailHandlers(
                        onProductClick = onProductClick,
                        onNavigateToHome = onNavigateToHome,
                        onSaveToggle = { product -> viewModel.action(ProductDetailAction.ToggleSave(product)) },
                        onAddToCart = {
                            viewModel.action(ProductDetailAction.AddToCart(state.product))
                            viewModel.action(ProductDetailAction.ShowBottomSheet(visible = false, type = BottomSheetType.NONE))
                        },
                        onBuyClick = { viewModel.action(ProductDetailAction.ShowBottomSheet()) },
                        onProcessPayment = { product -> viewModel.action(ProductDetailAction.ProcessPayment(product)) }
                    )

                    ProductDetailContent(
                        uiState = detailUIState,
                        bottomSheetState = bottomSheetUIState,
                        handlers = handlers,
                        viewModel = viewModel,
                        updateAppBarAlpha = { appBarAlpha = it }
                    )
                }

                is ProductDetailState.Error -> ErrorScreen(
                    onRetry = { viewModel.action(ProductDetailAction.Fetch) }
                )
            }

            ProductDetailAppBar(
                onBackClick = {
                    visible = false
                    onNavigateBack()
                },
                onCartClick = onCartClick,
                appBarAlpha = appBarAlpha,
                showCartButton = uiState is ProductDetailState.Content,
                onHeightChanged = { appBarHeight = it }
            )

            SnackbarHost(
                hostState = scaffoldState.snackBarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = BOTTOM_BAR_HEIGHT.dp)
                    .navigationBarsPadding(),
                snackbar = { snackbarData -> CustomSnackbar(snackbarData = snackbarData) }
            )
        }
    }
}

@Composable
fun ProductDetailContent(
    uiState: ProductDetailUIState,
    bottomSheetState: BottomSheetUIState,
    handlers: ProductDetailHandlers,
    viewModel: ProductDetailViewModel,
    updateAppBarAlpha: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val appBarAlpha by rememberAppBarAlphaState(lazyListState)
    val contentCornerRadius by rememberContentCornerRadiusState(lazyListState)
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val tabState = rememberTabStateHolder(lazyListState = lazyListState, appBarHeight = uiState.appBarHeight)

    LaunchedEffect(tabState.currentTabSection) {
        val currentSection = tabState.currentTabSection
        if (currentSection.index != tabState.selectedTabIndex) {
            tabState.updateSelectedIndex(currentSection.index)
        }
    }

    LaunchedEffect(appBarAlpha) { updateAppBarAlpha(appBarAlpha) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize()
        ) {
            item { ProductDetailImage(imageUrl = uiState.product.imageUrl) }
            item {
                ProductDetailBody(
                    state = ProductDetailState.Content(
                        product = uiState.product,
                        relatedProducts = uiState.relatedProducts
                    ),
                    contentCornerRadius = contentCornerRadius
                )
            }
            item(key = TAB_KEY) {
                ProductDetailTabs(
                    selectedTabIndex = tabState.selectedTabIndex,
                    onTabSelected = { index ->
                        tabState.updateSelectedIndex(index)
                        scope.launch {
                            tabState.animateToSection(TabSection.fromIndex(index))
                        }
                    },
                    modifier = Modifier
                        .offset(y = -CONTENT_OVERLAP.dp)
                        .onGloballyPositioned {
                            tabState.updateTabHeight(
                                with(density) { it.size.height.toDp() }
                            )
                        },
                    visible = uiState.tabVisible
                )
            }
            item { StyleInfo() }
            item {
                RecommendInfo(
                    relatedProducts = uiState.relatedProducts,
                    onProductClick = handlers.onProductClick,
                    onSaveToggle = handlers.onSaveToggle
                )
            }
        }

        ProductDetailTabs(
            selectedTabIndex = tabState.selectedTabIndex,
            onTabSelected = { index ->
                tabState.updateSelectedIndex(index)
                scope.launch {
                    tabState.animateToSection(TabSection.fromIndex(index))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .alpha(tabState.getVisibilityModifier())
                .padding(top = uiState.appBarHeight),
            visible = uiState.tabVisible
        )

        ProductBottomBar(
            price = formatPriceWithCommas(uiState.product.price.instantBuyPrice),
            isSaved = uiState.isSaved,
            onSaveToggle = { handlers.onSaveToggle(uiState.product) },
            onBuyClick = handlers.onBuyClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (bottomSheetState.isVisible) {
            val showBottomSheetState = remember { mutableStateOf(false) }

            LaunchedEffect(bottomSheetState.type) { showBottomSheetState.value = true }

            AnimatedCreamBottomSheet(
                showBottomSheet = showBottomSheetState,
                state = when (bottomSheetState.type) {
                    BottomSheetType.DETAIL -> BottomSheetState.Detail(
                        productImageUrl = uiState.product.imageUrl,
                        productName = uiState.product.productName,
                        productKo = uiState.product.ko,
                        onAddToCart = handlers.onAddToCart,
                        onBuyClick = {
                            viewModel.action(ProductDetailAction.ShowBottomSheet(
                                type = BottomSheetType.PAYMENT
                            ))
                        }
                    )

                    BottomSheetType.PAYMENT -> BottomSheetState.Payment(
                        products = listOf(uiState.product),
                        onPaymentClick = { handlers.onProcessPayment(uiState.product) }
                    )

                    BottomSheetType.PAYMENT_PROGRESS -> BottomSheetState.PaymentProgress(
                        status = bottomSheetState.paymentStatus,
                        onNavigateToHome = {
                            viewModel.action(ProductDetailAction.ShowBottomSheet(
                                visible = false,
                                type = BottomSheetType.NONE
                            ))
                            handlers.onNavigateToHome()
                        }
                    )

                    BottomSheetType.NONE -> BottomSheetState.None
                },
                onDismiss = {
                    if (bottomSheetState.paymentStatus != PaymentStatus.PROCESSING) {
                        viewModel.action(ProductDetailAction.ShowBottomSheet(
                            visible = false,
                            type = BottomSheetType.NONE,
                            status = PaymentStatus.NONE
                        ))
                    }
                },
                coroutineScope = scope
            )
        }
    }
}

@Composable
private fun ProductDetailBody(
    state: ProductDetailState.Content,
    contentCornerRadius: Dp
) {
    CreamSurface(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = -CONTENT_OVERLAP.dp)
            .clip(
                RoundedCornerShape(
                    topStart = contentCornerRadius,
                    topEnd = contentCornerRadius
                )
            ),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)
            ) {
                ProductInfoHeader(product = state.product)
                ProductInfoContainer(product = state.product)
                Divider()
                BenefitInfoContainer()
                Divider()
                ShippingInfoContainer()
            }
            ColorSpacer()
            BrandInfoContainer(brand = state.product.brand)
            ColorSpacer()
        }
    }
}

@Composable
private fun ProductDetailTabs(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean
) {
    val tabs = ProductDetailTab.entries.toList()

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier.shadow(4.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        text = { Text(tab.title) },
                        selected = selectedTabIndex == index,
                        onClick = { onTabSelected(index) }
                    )
                }
            }
        }
    }
}