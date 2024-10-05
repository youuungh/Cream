package com.ninezero.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ninezero.data.db.converter.CartConverter
import com.ninezero.domain.model.Brand
import com.ninezero.domain.model.Category
import com.ninezero.domain.model.Price
import com.ninezero.domain.model.Product

@Entity(tableName = "cart_products")
@TypeConverters(CartConverter::class)
class CartProductEntity(
    @PrimaryKey val productId: String,
    val productName: String,
    val ko: String,
    val imageUrl: String,
    val price: Price,
    val tradingVolume: Int,
    val releaseDate: String,
    val mainColor: String,
    val category: Category,
    val brand: Brand,
    val isNew: Boolean,
    val isFreeShipping: Boolean,
    val isSelected: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)

fun CartProductEntity.toDomain() = Product(
    productId = productId,
    productName = productName,
    ko = ko,
    imageUrl = imageUrl,
    price = price,
    tradingVolume = tradingVolume,
    releaseDate = releaseDate,
    mainColor = mainColor,
    category = category,
    brand = brand,
    isNew = isNew,
    isFreeShipping = isFreeShipping,
    isInCart = true,
    isSelected = isSelected,
    addedToCartAt = addedAt
)

fun Product.toCartProductEntity() = CartProductEntity(
    productId = productId,
    productName = productName,
    ko = ko,
    imageUrl = imageUrl,
    price = price,
    tradingVolume = tradingVolume,
    releaseDate = releaseDate,
    mainColor = mainColor,
    category = category,
    brand = brand,
    isNew = isNew,
    isFreeShipping = isFreeShipping,
    isSelected = isSelected,
    addedAt = addedToCartAt ?: System.currentTimeMillis()
)