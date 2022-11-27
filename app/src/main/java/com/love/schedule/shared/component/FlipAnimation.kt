package com.love.schedule.shared.component

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

enum class FlipState {
    FRONT, BACK
}

@Composable
fun FlipAnimation(
    state: FlipState,
    duration: Int = 300,
    frontModifier: Modifier = Modifier,
    backModifier: Modifier = Modifier,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation: Float by animateFloatAsState(
        targetValue = when (state) {
            FlipState.FRONT -> 0f
            FlipState.BACK -> 180f
        },
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing,
        )
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
    ) {
        Log.d("Animation", "rotation = $rotation")
        if (rotation <= 90f) {
            Box(
                modifier = frontModifier
                    .clip(shape = CircleShape)
            ) {
                front()
            }
        } else {
            Box(
                modifier = backModifier
                    .clip(shape = CircleShape)
                    .graphicsLayer {
                        rotationY = 180f
                    }
            ) {
                back()
            }
        }
    }
}