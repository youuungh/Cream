@file:OptIn(ExperimentalSharedTransitionApi::class)
package com.ninezero.cream.utils

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ninezero.di.R

val detailBoundsTransform = BoundsTransform { _, _ ->
    spatialExpressiveSpring()
}

enum class ProductDetailTab(val title: String) {
    STYLE("스타일"),
    RECOMMENDATIONS("추천");

    companion object {
        fun fromIndex(index: Int): ProductDetailTab = entries[index]
    }
}

enum class CategorySharedElementType {
    Bounds, Image, Title, Background
}

data class CategorySharedElementKey(
    val categoryId: String,
    val categoryName: String,
    val type: CategorySharedElementType
)

data class AnimState (
    val enterTransition: EnterTransition,
    val exitTransition: ExitTransition
)

@Composable
fun rememberSlideInOutAnimState(): AnimState {
    val enterTransition = remember {
        //fadeIn(animationSpec = tween(ANIMATION_DURATION, easing = LinearOutSlowInEasing)) +
        slideInVertically(
            initialOffsetY = { it / 10 },
            animationSpec = tween(ANIMATION_DURATION, easing = FastOutSlowInEasing)
        )
    }
    val exitTransition = remember {
        //fadeOut(animationSpec = tween(ANIMATION_DURATION, easing = LinearOutSlowInEasing)) +
        slideOutVertically(
            targetOffsetY = { it / 10 },
            animationSpec = tween(ANIMATION_DURATION, easing = FastOutSlowInEasing)
        )
    }

    return remember { AnimState(enterTransition, exitTransition) }
}

fun <T> spatialExpressiveSpring() = spring<T>(
    dampingRatio = 0.8f,
    stiffness = 380f
)

fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f
)

fun getCategoryImageResource(categoryId: String) = R.drawable::class.java.getField("category_img_$categoryId").getInt(null)