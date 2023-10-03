/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect

internal class Circle : Figure() {
    var centerX: Float by visualProp(0.0f)
    var centerY: Float by visualProp(0.0f)
    var radius: Float by visualProp(0.0f)

    override fun render(canvas: Canvas) {
        fillPaint?.let {
            canvas.drawCircle(centerX, centerY, radius, it)
        }
        strokePaint?.let {
            canvas.drawCircle(centerX, centerY, radius, it)
        }
    }

    override val localBounds: Rect
        get() = Rect.makeXYWH(
            centerX - radius,
            centerY - radius,
            radius * 2,
            radius * 2
        )
}