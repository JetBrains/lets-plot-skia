/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasProvider
import org.jetbrains.letsPlot.core.canvas.Font

class TextMeasurer private constructor(
    private val canvas: Canvas
) {

    fun measureTextWidth(text: String, font: Font): Float {
        with(canvas.context2d) {
            save()
            setFont(font)
            val width = measureText(text)
            restore()
            return width.toFloat()
        }
    }

    companion object {
        fun create(canvasProvider: CanvasProvider): TextMeasurer {
            return TextMeasurer(canvasProvider.createCanvas(Vector(0, 0)))
        }
    }
}
