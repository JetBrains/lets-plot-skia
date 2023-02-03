/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia.mapper.drawing

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect
import kotlin.math.max
import kotlin.math.min

internal class Line: Figure() {
    var x0: Float by visualProp(0.0f)
    var y0: Float by visualProp(0.0f)
    var x1: Float by visualProp(0.0f)
    var y1: Float by visualProp(0.0f)

    override fun doDraw(canvas: Canvas) {
        strokePaint?.let { canvas.drawLine(x0, y0, x1, y1, it) }
    }

    override fun doGetBounds(): Rect {
        return Rect.makeLTRB(min(x0, x1), min(y0, y1), max(x0, x1), max(y0, y1)).offset(absoluteOffsetX, absoluteOffsetY)
    }
}