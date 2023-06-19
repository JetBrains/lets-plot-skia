/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Path
import org.jetbrains.skia.Rect

internal class Line : Figure() {
    var x0: Float by visualProp(0.0f)
    var y0: Float by visualProp(0.0f)
    var x1: Float by visualProp(0.0f)
    var y1: Float by visualProp(0.0f)

    override fun doDraw(canvas: Canvas) {
        strokePaint?.let { canvas.drawLine(x0, y0, x1, y1, it) }
    }

    override val localBounds: Rect
        get() {
            // TODO: pref. Cache.
            val path = Path().moveTo(x0, y0).lineTo(x1, y1)
            return (strokePaint?.getFillPath(path) ?: path).bounds
        }
}