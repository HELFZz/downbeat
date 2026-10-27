package com.downloader.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WavyProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    strokeWidth: Dp = 3.dp,
    amplitude: Dp = 3.5.dp,
    wave: Boolean = true,
) {
    val transition = rememberInfiniteTransition(label = "wavy-progress")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave-phase",
    )

    Canvas(modifier.fillMaxWidth().height(14.dp)) {
        val centerY = size.height / 2f
        val stroke = strokeWidth.toPx()
        val dotRadius = stroke * 0.85f
        val endX = size.width - dotRadius
        if (endX <= 0f) return@Canvas

        val clamped = progress.coerceIn(0f, 1f)
        val activeX = (endX * clamped).coerceIn(0f, endX)
        val height = if (wave) amplitude.toPx() else 0f
        val wavelength = 14.dp.toPx()
        val gap = stroke * 1.6f

        if (activeX > 0.5f) {
            val path = Path()
            val step = 1.5.dp.toPx()
            var x = 0f
            var first = true
            while (x < activeX) {
                val y = centerY + sin((x / wavelength) * 2f * PI.toFloat() - phase) * height
                if (first) {
                    path.moveTo(x, y)
                    first = false
                } else {
                    path.lineTo(x, y)
                }
                x += step
            }
            val endY = centerY + sin((activeX / wavelength) * 2f * PI.toFloat() - phase) * height
            path.lineTo(activeX, endY)
            drawPath(
                path = path,
                color = activeColor,
                style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }

        val trackStart = (activeX + gap).coerceAtMost(endX)
        if (trackStart < endX) {
            drawLine(
                color = trackColor,
                start = Offset(trackStart, centerY),
                end = Offset(endX, centerY),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
        drawCircle(color = activeColor, radius = dotRadius, center = Offset(endX, centerY))
    }
}
