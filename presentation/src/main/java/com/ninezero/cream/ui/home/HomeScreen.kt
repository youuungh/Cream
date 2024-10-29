@file:OptIn(ExperimentalSharedTransitionApi::class)
package com.ninezero.cream.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ninezero.cream.ui.component.SingleBanner
import com.ninezero.cream.ui.component.BrandCard
import com.ninezero.cream.ui.component.Divider
import com.ninezero.cream.ui.component.GridSection
import com.ninezero.cream.ui.component.ProductCard
import com.ninezero.cream.ui.component.RowSection
import com.ninezero.cream.base.collectAsState
import com.ninezero.cream.base.collectEvents
import com.ninezero.cream.ui.component.CreamPullRefresh
import com.ninezero.cream.ui.component.CreamScaffold
import com.ninezero.cream.ui.component.CreamSurface
import com.ninezero.cream.ui.component.CustomDialog
import com.ninezero.cream.ui.component.CustomSnackbar
import com.ninezero.cream.ui.component.EmptyScreen
import com.ninezero.cream.ui.component.ErrorScreen
import com.ninezero.cream.ui.component.SearchHistorySection
import com.ninezero.cream.ui.component.SearchResultsSection
import com.ninezero.cream.ui.component.SearchTopAppBar
import com.ninezero.cream.ui.component.SuggestionsSection
import com.ninezero.cream.ui.component.TopBanner
import com.ninezero.cream.ui.component.bottomsheet.BottomSheetState
import com.ninezero.cream.ui.component.bottomsheet.CreamBottomSheet
import com.ninezero.cream.ui.component.rememberCreamScaffoldState
import com.ninezero.cream.ui.component.skeleton.HomeSkeleton
import com.ninezero.cream.ui.home.search.SearchAction
import com.ninezero.cream.ui.home.search.SearchEvent
import com.ninezero.cream.ui.home.search.SearchState
import com.ninezero.cream.utils.SearchSortOption
import com.ninezero.cream.viewmodel.HomeViewModel
import com.ninezero.cream.viewmodel.SearchViewModel
import com.ninezero.di.R
import com.ninezero.domain.model.HomeData
import com.ninezero.domain.model.Product

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onCartClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSaved: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val homeUiState by homeViewModel.state.collectAsState()
    val isRefresh by homeViewModel.isRefresh.collectAsState()
    val searchUiState by searchViewModel.state.collectAsState()
    val searchQuery by searchViewModel.query.collectAsState()
    val isSearchMode by searchViewModel.isSearchMode.collectAsState()

    val scaffoldState = rememberCreamScaffoldState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val backHandlingEnabled by remember { derivedStateOf { isSearchMode } }

    homeViewModel.collectEvents {
        when (it) {
            is HomeEvent.NavigateToProductDetail -> onProductClick(it.productId)
            is HomeEvent.NavigateToLogin -> onNavigateToLogin()
            is HomeEvent.NavigateToSaved -> onNavigateToSaved()
            is HomeEvent.ShowSnackbar -> scaffoldState.showSnackbar(it.message)
        }
    }

    searchViewModel.collectEvents {
        when (it) {
            is SearchEvent.NavigateToLogin -> onNavigateToLogin()
            is SearchEvent.NavigateToSaved -> onNavigateToSaved()
            is SearchEvent.ShowSnackbar -> scaffoldState.showSnackbar(it.message)
        }
    }

    BackHandler(backHandlingEnabled) {
        keyboardController?.hide()
        focusManager.clearFocus()
        searchViewModel.clearSearch()
        searchViewModel.setSearchMode(false)
    }

    CreamSurface(modifier = modifier.fillMaxSize()) {
        SharedTransitionLayout {
            CreamScaffold(
                topBar = {
                    SearchTopAppBar(
                        isSearchMode = isSearchMode,
                        isSearchResultMode = isSearchMode && searchUiState is SearchState.Results,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchViewModel.updateQuery(it) },
                        onSearch = {
                            searchViewModel.search(it)
                            searchViewModel.setSearchMode(true)
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        },
                        onBackClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            searchViewModel.clearSearch()
                            searchViewModel.setSearchMode(false)
                        },
                        onClearClick = {
                            searchViewModel.clearSearch()
                            searchViewModel.setSearchMode(true)
                        },
                        onCartClick = onCartClick,
                        onSearchClick = { searchViewModel.setSearchMode(true) },
                        focusRequester = focusRequester
                    )
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = it,
                        snackbar = { snackbarData -> CustomSnackbar(snackbarData) }
                    )
                },
                snackbarHostState = scaffoldState.snackBarHostState
            ) { innerPadding ->
                CreamPullRefresh(
                    refreshing = isRefresh,
                    onRefresh = { homeViewModel.refreshData() }
                ) {
                    AnimatedContent(
                        targetState = isSearchMode,
                        transitionSpec = {
                            if (targetState) {
                                slideInVertically { height -> -height } + fadeIn() togetherWith
                                        slideOutVertically { height -> height } + fadeOut()
                            } else {
                                slideInVertically { height -> height } + fadeIn() togetherWith
                                        slideOutVertically { height -> -height } + fadeOut()
                            }
                        },
                        label = "search_content"
                    ) { searchMode ->
                        if (searchMode) {
                            SearchContent(
                                state = searchUiState,
                                onProductClick = onProductClick,
                                searchViewModel = searchViewModel,
                                onClearAndHideHistory = { showClearHistoryDialog = true },
                                onSuggestionClick = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    searchViewModel.search(it)
                                    searchViewModel.setSearchMode(true)
                                },
                                onSortClick = { showBottomSheet = true },
                                focusRequester = focusRequester,
                                keyboardController = keyboardController,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                                    .pointerInput(Unit) {
                                        detectTapGestures(onTap = {
                                            focusManager.clearFocus()
                                            keyboardController?.hide()
                                        })
                                    }
                            )
                        } else {
                            when (val state = homeUiState) {
                                is HomeState.Fetching -> HomeSkeleton(modifier = Modifier.padding(innerPadding))

                                is HomeState.Content -> HomeContent(
                                    data = state.homeData,
                                    onProductClick = { productId -> homeViewModel.action(HomeAction.ProductClicked(productId)) },
                                    onSaveClick = { product -> homeViewModel.action(HomeAction.ToggleSave(product)) },
                                    onBrandClick = { /*TODO*/ },
                                    modifier = Modifier.padding(innerPadding)
                                )

                                is HomeState.Error -> ErrorScreen(
                                    onRetry = { homeViewModel.action(HomeAction.Fetch) },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        CreamBottomSheet(
            showBottomSheet = remember { mutableStateOf(showBottomSheet) },
            state = BottomSheetState.SearchSort(
                selectedOption = (searchUiState as? SearchState.Results)?.sortOption
                    ?: SearchSortOption.RECOMMENDED,
                onOptionSelected = { option ->
                    searchViewModel.action(SearchAction.ChangeSort(option))
                    showBottomSheet = false
                }
            ),
            onDismiss = { showBottomSheet = false },
            coroutineScope = rememberCoroutineScope()
        )
    }

    if (showClearHistoryDialog) {
        CustomDialog(
            onDismissRequest = {
                showClearHistoryDialog = false
                if (isSearchMode) {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            },
            title = stringResource(R.string.dialog_clear_search_history),
            content = stringResource(R.string.dialog_clear_search_history_confirm),
            confirmButtonText = stringResource(R.string.delete),
            onConfirmClick = {
                searchViewModel.action(SearchAction.ClearHistory)
                showClearHistoryDialog = false
            }
        )
    }
}

@Composable
private fun HomeContent(
    data: HomeData,
    onProductClick: (String) -> Unit,
    onSaveClick: (Product) -> Unit,
    onBrandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { TopBanner(banners = data.topBanners) }
        item {
            key("just_dropped") {
                RowSection(
                    title = stringResource(id = R.string.just_dropped),
                    subtitle = stringResource(id = R.string.just_dropped_subtitle),
                    products = data.justDropped,
                    onProductClick = onProductClick,
                    onSaveClick = onSaveClick
                )
            }
        }
        item { Divider() }
        item {
            key("most_popular") {
                RowSection(
                    title = stringResource(id = R.string.most_popular),
                    subtitle = stringResource(id = R.string.most_popular_subtitle),
                    products = data.mostPopular,
                    onProductClick = onProductClick,
                    onSaveClick = onSaveClick
                )
            }
        }
        item { data.banner?.let { key(it.bannerId) { SingleBanner(banner = it) } } }
        item {
            key("for_you") {
                GridSection(
                    title = stringResource(id = R.string.for_you),
                    subtitle = stringResource(id = R.string.for_you_subtitle),
                    items = data.forYou,
                    rows = 2,
                    height = 550
                ) {
                    ProductCard(
                        product = it,
                        onClick = { onProductClick(it.productId) },
                        onSaveToggle = { onSaveClick(it) },
                        modifier = Modifier.width(160.dp)
                    )
                }
            }
        }
        item { Divider() }
        item {
            key("top_brand") {
                GridSection(
                    title = stringResource(id = R.string.top_brand),
                    subtitle = stringResource(id = R.string.top_brand_subtitle),
                    items = data.brands,
                    rows = 3,
                    height = 260
                ) {
                    BrandCard(
                        brand = it,
                        onClick = { onBrandClick(it.brandId) }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchContent(
    state: SearchState,
    onProductClick: (String) -> Unit,
    searchViewModel: SearchViewModel,
    onClearAndHideHistory: () -> Unit,
    onSuggestionClick: (String) -> Unit,
    onSortClick: () -> Unit,
    focusRequester: FocusRequester,
    keyboardController: SoftwareKeyboardController?,
    modifier: Modifier = Modifier
) {
    CreamSurface(modifier = modifier) {
        when (state) {
            is SearchState.Init -> {
                if (!state.isHistoryHidden) {
                    SearchHistorySection(
                        searchHistory = state.history,
                        onHistoryItemClick = onSuggestionClick,
                        onClearAndHideHistory = {
                            keyboardController?.hide()
                            onClearAndHideHistory()
                        },
                        onRemoveHistoryItem = { searchViewModel.action(SearchAction.RemoveHistory(it)) }
                    )
                }
            }
            is SearchState.Suggesting -> {
                SuggestionsSection(
                    suggestions = state.suggestions,
                    searchQuery = state.query,
                    onSuggestionClick = onSuggestionClick
                )
            }
            is SearchState.Searching -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is SearchState.Results -> {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    if (state.products.isEmpty()) {
                        EmptyScreen(title = stringResource(R.string.no_search_results))
                    }
                    SearchResultsSection(
                        products = state.products,
                        sortOption = state.sortOption,
                        onSortClick = onSortClick,
                        onProductClick = onProductClick,
                        onSaveToggle = { searchViewModel.action(SearchAction.ToggleSave(it)) }
                    )
                }
            }
            is SearchState.Error -> ErrorScreen(
                onRetry = {
                    val currentQuery = searchViewModel.query.value
                    if (currentQuery.isNotBlank()) {
                        searchViewModel.search(currentQuery)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    LaunchedEffect(state) {
        if (state is SearchState.Init || state is SearchState.Suggesting) { focusRequester.requestFocus() }
    }
}