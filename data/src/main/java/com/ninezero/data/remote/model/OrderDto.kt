package com.ninezero.data.remote.model

import com.google.firebase.Timestamp
import com.ninezero.domain.model.Brand
import com.ninezero.domain.model.Category
import com.ninezero.domain.model.Order
import com.ninezero.domain.model.OrderStatus
import com.ninezero.domain.model.Price
import com.ninezero.domain.model.PriceStatus
import com.ninezero.domain.model.Product

data class OrderDto(
    val orderId: String = "",
    val userId: String = "",
    val products: List<Map<String, Any>> = emptyList(),
    val totalAmount: Int = 0,
    val status: String = "",
    val orderDate: Timestamp = Timestamp.now()
)

fun OrderDto.toDomain(): Order = Order(
    orderId = orderId,
    userId = userId,
    products = products.map {
        Product(
            productId = it["productId"] as String,
            productName = it["productName"] as String,
            ko = it["ko"] as String,
            imageUrl = it["imageUrl"] as String,
            price = (it["price"] as Map<*, *>).let { priceMap ->
                Price(
                    releasePrice = priceMap["releasePrice"] as? Int,
                    instantBuyPrice = (priceMap["instantBuyPrice"] as Number).toInt(),
                    status = PriceStatus.valueOf(priceMap["status"] as String)
                )
            },
            tradingVolume = (it["tradingVolume"] as Number).toInt(),
            releaseDate = it["releaseDate"] as String,
            mainColor = it["mainColor"] as String,
            category = (it["category"] as Map<*, *>).let { categoryMap ->
                Category(
                    categoryId = categoryMap["categoryId"] as String,
                    categoryName = categoryMap["categoryName"] as String,
                    ko = categoryMap["ko"] as String
                )
            },
            brand = (it["brand"] as Map<*, *>).let { brandMap ->
                Brand(
                    brandId = brandMap["brandId"] as String,
                    brandName = brandMap["brandName"] as String,
                    ko = brandMap["ko"] as String,
                    imageUrl = brandMap["imageUrl"] as String
                )
            },
            isNew = it["isNew"] as Boolean,
            isFreeShipping = it["isFreeShipping"] as Boolean
        )
    },
    totalAmount = totalAmount,
    status = OrderStatus.valueOf(status),
    orderDate = orderDate.toDate()
)

fun Order.toDto(): OrderDto = OrderDto(
    orderId = orderId,
    userId = userId,
    products = products.map {
        mapOf(
            "productId" to it.productId,
            "productName" to it.productName,
            "ko" to it.ko,
            "imageUrl" to it.imageUrl,
            "price" to it.price,
            "tradingVolume" to it.tradingVolume,
            "releaseDate" to it.releaseDate,
            "mainColor" to it.mainColor,
            "category" to it.category,
            "brand" to it.brand,
            "isNew" to it.isNew,
            "isFreeShipping" to it.isFreeShipping
        )
    },
    totalAmount = totalAmount,
    status = status.name,
    orderDate = Timestamp(orderDate)
)