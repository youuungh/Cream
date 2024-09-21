@file:OptIn(ExperimentalSharedTransitionApi::class)
package com.ninezero.cream.utils

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.spring
import com.ninezero.di.R

fun <T> spatialExpressiveSpring() = spring<T>(
    dampingRatio = 0.8f,
    stiffness = 380f
)

fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f
)

val detailBoundsTransform = BoundsTransform { _, _ ->
    spatialExpressiveSpring()
}

fun getCategoryImageResource(categoryId: String) =
    R.drawable::class.java.getField("category_img_$categoryId").getInt(null)