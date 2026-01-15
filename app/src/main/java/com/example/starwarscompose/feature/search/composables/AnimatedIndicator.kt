package com.example.starwarscompose.feature.search.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedIndicator(
    animationType: AnimationType,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "indicator")

    when (animationType) {
        AnimationType.PULSE_RED -> PulseAnimation(animationType, infiniteTransition, modifier)
        AnimationType.ROTATE_BLUE -> RotateAnimation(animationType, infiniteTransition, modifier)
        AnimationType.SCALE_GREEN -> ScaleAnimation(animationType, infiniteTransition, modifier)
        AnimationType.ORBIT_PURPLE -> OrbitAnimation(animationType, infiniteTransition, modifier)
        AnimationType.FADE_GRAY -> FadeAnimation(animationType, infiniteTransition, modifier)
    }
}

@Composable
private fun PulseAnimation(
    animationType: AnimationType,
    infiniteTransition: androidx.compose.animation.core.InfiniteTransition,
    modifier: Modifier
) {
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier.size(40.dp)) {
        drawCircle(
            color = animationType.secondaryColor,
            radius = size.minDimension / 2 * scale
        )
        drawCircle(
            color = animationType.primaryColor,
            radius = size.minDimension / 3 * scale
        )
    }
}

@Composable
private fun RotateAnimation(
    animationType: AnimationType,
    infiniteTransition: androidx.compose.animation.core.InfiniteTransition,
    modifier: Modifier
) {
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    Canvas(modifier = modifier.size(40.dp)) {
        drawCircle(
            color = animationType.secondaryColor,
            radius = size.minDimension / 3
        )
        rotate(rotation) {
            drawArc(
                color = animationType.primaryColor,
                startAngle = 0f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }
}

@Composable
private fun ScaleAnimation(
    animationType: AnimationType,
    infiniteTransition: androidx.compose.animation.core.InfiniteTransition,
    modifier: Modifier
) {
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Canvas(modifier = modifier.size(40.dp)) {
        val rectSize = size.minDimension * 0.6f * scale
        val topLeft = Offset(
            (size.width - rectSize) / 2,
            (size.height - rectSize) / 2
        )
        drawRect(
            color = animationType.secondaryColor,
            topLeft = topLeft,
            size = androidx.compose.ui.geometry.Size(rectSize, rectSize)
        )
        val innerSize = rectSize * 0.6f
        val innerTopLeft = Offset(
            (size.width - innerSize) / 2,
            (size.height - innerSize) / 2
        )
        drawRect(
            color = animationType.primaryColor,
            topLeft = innerTopLeft,
            size = androidx.compose.ui.geometry.Size(innerSize, innerSize)
        )
    }
}

@Composable
private fun OrbitAnimation(
    animationType: AnimationType,
    infiniteTransition: androidx.compose.animation.core.InfiniteTransition,
    modifier: Modifier
) {
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    Canvas(modifier = modifier.size(40.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val orbitRadius = size.minDimension / 3

        drawCircle(
            color = animationType.primaryColor,
            radius = size.minDimension / 6,
            center = Offset(centerX, centerY)
        )

        for (i in 0 until 3) {
            val dotAngle = Math.toRadians((angle + i * 120).toDouble())
            val dotX = centerX + orbitRadius * cos(dotAngle).toFloat()
            val dotY = centerY + orbitRadius * sin(dotAngle).toFloat()
            drawCircle(
                color = animationType.secondaryColor,
                radius = 4.dp.toPx(),
                center = Offset(dotX, dotY)
            )
        }
    }
}

@Composable
private fun FadeAnimation(
    animationType: AnimationType,
    infiniteTransition: androidx.compose.animation.core.InfiniteTransition,
    modifier: Modifier
) {
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fade"
    )

    Canvas(modifier = modifier.size(40.dp)) {
        drawCircle(
            color = animationType.secondaryColor.copy(alpha = alpha * 0.5f),
            radius = size.minDimension / 2
        )
        drawCircle(
            color = animationType.primaryColor.copy(alpha = alpha),
            radius = size.minDimension / 3
        )
    }
}
