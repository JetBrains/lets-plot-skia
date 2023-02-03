/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia.mapper.drawing

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect

internal class Circle: Figure() {
    var centerX: Float by visualProp(0.0f)
    var centerY: Float by visualProp(0.0f)
    var radius: Float by visualProp(0.0f)

    override fun doDraw(canvas: Canvas) {
        fillPaint?.let {
            canvas.drawCircle(centerX, centerY, radius, it)
        }
        strokePaint?.let {
            canvas.drawCircle(centerX, centerY, radius, it)
        }
    }

    override fun doGetBounds(): Rect {
        return Rect.makeXYWH(
            centerX - radius,
            centerY - radius,
            radius * 2,
            radius * 2
        ).offset(absoluteOffsetX, absoluteOffsetY)
    }
}