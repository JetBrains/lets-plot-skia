/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect

internal class Ellipse : Figure() {
    var centerX: Float by visualProp(0.0f)
    var centerY: Float by visualProp(0.0f)
    var radiusX: Float by visualProp(0.0f)
    var radiusY: Float by visualProp(0.0f)

    private val rect: Rect by computedProp(Ellipse::centerX, Ellipse::centerY, Ellipse::radiusX, Ellipse::radiusY) {
        Rect(
            left = centerX - radiusX,
            top = centerY - radiusY,
            right = centerX + radiusX,
            bottom = centerY + radiusY
        )
    }

    override fun render(canvas: Canvas) {
        fillPaint?.let { canvas.drawOval(rect, it) }
        strokePaint?.let { canvas.drawOval(rect, it) }
    }

    override val localBounds: Rect
        get() = Rect.makeXYWH(centerX - radiusX, centerY - radiusY, radiusX * 2, radiusY * 2)
}