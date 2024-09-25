package com.ninezero.data.db.converter

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.ninezero.domain.model.Brand
import com.ninezero.domain.model.Category
import com.ninezero.domain.model.Price

class SaveConverter {
    private val gson = GsonBuilder().create()

    @TypeConverter
    fun fromPrice(value: Price) : String = gson.toJson(value)

    @TypeConverter
    fun toPrice(priceString: String): Price = gson.fromJson(priceString, Price::class.java)

    @TypeConverter
    fun fromCategory(category: Category): String = gson.toJson(category)

    @TypeConverter
    fun toCategory(categoryString: String): Category = gson.fromJson(categoryString, Category::class.java)

    @TypeConverter
    fun fromBrand(brand: Brand): String = gson.toJson(brand)

    @TypeConverter
    fun toBrand(brandString: String): Brand = gson.fromJson(brandString, Brand::class.java)
}