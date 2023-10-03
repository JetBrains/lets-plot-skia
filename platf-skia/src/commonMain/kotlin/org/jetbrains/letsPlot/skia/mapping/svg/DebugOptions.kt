/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.mapping.svg

import org.jetbrains.letsPlot.skia.shape.*
import org.jetbrains.skia.*

internal object DebugOptions {
    const val DEBUG_DRAWING_ENABLED: Boolean = false
    const val VALIDATE_MANAGED_PROPERTIES = false

    fun drawBoundingBoxes(rootElement: Pane, canvas: Canvas, scaleMatrix: Matrix33) {
        val strokePaint = Paint().setStroke(true)
        val fillPaint = Paint().setStroke(false)

        canvas.save()
        canvas.setMatrix(scaleMatrix)
        depthFirstTraversal(rootElement) {
            val bounds = it.screenBounds

            val color = when (it) {
                is Pane -> Color.CYAN
                is Group -> Color.YELLOW
                is Text -> Color.GREEN
                is Rectangle -> Color.BLUE
                is Circle -> Color.RED
                is Line -> Color.RED
                else -> Color.MAGENTA
            }.let(::Color4f)

            val strokeWidth = when (it) {
                is Pane, is Group -> 3f
                else -> 1f
            }

            fillPaint.color = color.withA(0.1f).toColor()
            strokePaint.color = color.toColor()
            strokePaint.strokeWidth = strokeWidth
            canvas.drawRect(bounds, fillPaint)
            canvas.drawRect(bounds, strokePaint)

        }
        canvas.restore()

        strokePaint.close()
        fillPaint.close()
    }
}
