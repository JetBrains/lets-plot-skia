package org.jetbrains.letsPlot.skia.compose.android

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

internal class ColorRect(val color: Int) : Drawable() {
    override fun draw(canvas: Canvas) {
        canvas.drawRect(
            canvas.clipBounds,
            Paint().also { it.color = color }
        )
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}