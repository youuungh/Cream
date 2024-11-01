@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.ninezero.cream.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import com.ninezero.di.R

object ErrorHandler {
    fun getErrorMessage(e: Throwable): String {
        return e.message ?: UNKNOWN_ERROR
    }
}

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

data class AnimState(
    val enterTransition: EnterTransition,
    val exitTransition: ExitTransition
)

fun <T> spatialExpressiveSpring() = spring<T>(
    dampingRatio = 0.8f,
    stiffness = 380f
)

fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f
)

fun getCategoryImageResource(categoryId: String) =
    R.drawable::class.java.getField("category_img_$categoryId").getInt(null)

@Composable
fun rememberSlideInOutAnimState(): AnimState {
    val enterTransition = remember {
        fadeIn(animationSpec = tween(durationMillis = 150)) +
                slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) { it }
    }
    val exitTransition = remember {
        fadeOut(animationSpec = tween(durationMillis = 300)) +
                slideOutVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) { it / 10 }
    }

    return remember { AnimState(enterTransition, exitTransition) }
}

@Composable
fun rememberProductDetailAnimState(): Pair<
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition,
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition
        > {
    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(animationSpec = tween(durationMillis = 150)) +
                slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) { it }
    }
    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(animationSpec = tween(durationMillis = 300)) +
                slideOutVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) { it / 10 }
    }

    return remember { enterTransition to exitTransition }
}