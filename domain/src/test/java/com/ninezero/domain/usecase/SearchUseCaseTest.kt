package com.ninezero.domain.usecase

import com.ninezero.domain.model.Brand
import com.ninezero.domain.model.Category
import com.ninezero.domain.model.EntityWrapper
import com.ninezero.domain.model.Price
import com.ninezero.domain.model.PriceStatus
import com.ninezero.domain.model.Product
import com.ninezero.domain.model.SearchHistory
import com.ninezero.domain.repository.ProductRepository
import com.ninezero.domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class SearchUseCaseTest {

    private lateinit var searchUseCase: SearchUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var searchRepository: SearchRepository

    // 테스트용 샘플 데이터
    private val sampleProduct1 = Product(
        productId = "1",
        productName = "Test Product",
        ko = "테스트 상품",
        imageUrl = "test.jpg",
        price = Price(100000, 90000, PriceStatus.FOR_SALE),
        tradingVolume = 100,
        releaseDate = "2024-01-01",
        mainColor = "Black",
        category = Category("1", "Test Category", "테스트 카테고리"),
        brand = Brand("1", "Test Brand", "테스트 브랜드", "brand.jpg"),
        isNew = true,
        isFreeShipping = true,
        isSaved = false
    )

    private val sampleProduct2 = sampleProduct1.copy(
        productId = "2",
        productName = "Another Product",
        ko = "다른 상품"
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        searchUseCase = SearchUseCase(productRepository, searchRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchProducts with empty keyword returns empty list`() = runTest {
        val results = searchUseCase.searchProducts("").toList()

        assertTrue(results.isNotEmpty())
        assertTrue(results[0] is EntityWrapper.Success)
        assertEquals(0, (results[0] as EntityWrapper.Success).entity.size)
    }

    @Test
    fun `searchProducts with matching keyword returns filtered products`() = runTest {
        // Given
        val products = listOf(sampleProduct1, sampleProduct2)
        `when`(productRepository.getAllProducts()).thenReturn(flowOf(products))

        // When
        val results = searchUseCase.searchProducts("Test Product").toList()

        // Then
        assertTrue(results.isNotEmpty())
        assertTrue(results[0] is EntityWrapper.Success)
        assertEquals(1, (results[0] as EntityWrapper.Success).entity.size)
        assertEquals("Test Product", (results[0] as EntityWrapper.Success).entity[0].productName)
    }

    @Test
    fun `getSearchHistory returns history from repository`() = runTest {
        val histories = listOf(
            SearchHistory("test1", System.currentTimeMillis()),
            SearchHistory("test2", System.currentTimeMillis())
        )
        `when`(searchRepository.getSearchHistory()).thenReturn(flowOf(histories))

        val result = searchUseCase.getSearchHistory().toList()

        assertEquals(1, result.size)
        assertEquals(2, result[0].size)
        assertEquals("test1", result[0][0].keyword)
    }

    @Test
    fun `addSearchHistory delegates to repository`() = runTest {
        searchUseCase.addSearchHistory("test")

        verify(searchRepository).addSearchHistory("test")
    }

    @Test
    fun `removeSearchHistory delegates to repository`() = runTest {
        searchUseCase.removeSearchHistory("test")

        verify(searchRepository).removeSearchHistory("test")
    }

    @Test
    fun `clearSearchHistory delegates to repository`() = runTest {
        searchUseCase.clearSearchHistory()

        verify(searchRepository).clearSearchHistory()
    }

    @Test
    fun `hideSearchHistory delegates to repository`() = runTest {
        searchUseCase.hideSearchHistory()

        verify(searchRepository).hideSearchHistory()
    }

    @Test
    fun `isSearchHistoryHidden returns value from repository`() = runTest {
        `when`(searchRepository.isSearchHistoryHidden()).thenReturn(flowOf(true))

        val result = searchUseCase.isSearchHistoryHidden().toList()

        assertEquals(1, result.size)
        assertEquals(true, result[0])
    }

    @Test
    fun `getSuggestedKeywords returns combined suggestions`() = runTest {
        val products = listOf(sampleProduct1, sampleProduct2)
        val histories = listOf(
            SearchHistory("test suggestion", System.currentTimeMillis())
        )

        `when`(productRepository.getAllProducts()).thenReturn(flowOf(products))
        `when`(searchRepository.getSearchHistory()).thenReturn(flowOf(histories))

        val result = searchUseCase.getSuggestedKeywords("test").toList()

        assertEquals(1, result.size)
        assertTrue(result[0].contains("Test Product"))
        assertTrue(result[0].contains("Test Brand"))
        assertTrue(result[0].contains("test suggestion"))
    }
}