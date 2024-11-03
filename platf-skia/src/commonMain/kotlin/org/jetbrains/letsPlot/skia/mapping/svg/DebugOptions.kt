/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.mapping.svg

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.skia.shape.*
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PathEffect

internal object DebugOptions {
    const val DEBUG_DRAWING_ENABLED: Boolean = false

    fun drawBoundingBoxes(rootElement: Pane, canvas: Canvas) {
        val strokePaint = Paint().setStroke(true)
        val fillPaint = Paint().setStroke(false)

        depthFirstTraversal(rootElement).forEach { el ->
            val color = when (el) {
                is Pane -> Color.CYAN
                is Group -> Color.YELLOW
                is Text -> Color.GREEN
                is Rectangle -> Color.BLUE
                is Circle -> Color.RED
                is Line -> Color.RED
                else -> Color.LIGHT_GRAY
            }.asSkiaColor

            fillPaint.color = color.withA(0.02f).toColor()
            canvas.drawRect(el.screenBounds, fillPaint)

            strokePaint.color = color.withA(0.7f).toColor()
            strokePaint.strokeWidth = if(el is Container) 3f else 1f
            strokePaint.pathEffect = if (el is Container) PathEffect.makeDash(floatArrayOf(3f, 8f), 0f) else null
            canvas.drawRect(el.screenBounds, strokePaint)
        }

        strokePaint.close()
        fillPaint.close()
    }
}
