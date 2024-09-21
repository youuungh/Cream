package com.ninezero.cream.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

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