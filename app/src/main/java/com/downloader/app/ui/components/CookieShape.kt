package com.downloader.app.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Soft scalloped "cookie" used for the empty state and loading disc,
 * mirroring the Material 3 Expressive shape vocabulary.
 */
class CookieShape(private val lobes: Int = 9, private val bulge: Float = 1.14f) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path()
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = min(size.width, size.height) / 2f * 0.9f
        val segments = lobes * 2

        fun point(index: Int, r: Float): Offset {
            val angle = (2.0 * PI * index / segments) - PI / 2.0
            return Offset(cx + cos(angle).toFloat() * r, cy + sin(angle).toFloat() * r)
        }

        val start = point(0, radius)
        path.moveTo(start.x, start.y)
        for (index in 1..segments) {
            val end = point(index, radius)
            val midAngle = (2.0 * PI * (index - 0.5) / segments) - PI / 2.0
            val control = Offset(
                cx + cos(midAngle).toFloat() * radius * bulge,
                cy + sin(midAngle).toFloat() * radius * bulge,
            )
            path.quadraticBezierTo(control.x, control.y, end.x, end.y)
        }
        path.close()
        return Outline.Generic(path)
    }
}
