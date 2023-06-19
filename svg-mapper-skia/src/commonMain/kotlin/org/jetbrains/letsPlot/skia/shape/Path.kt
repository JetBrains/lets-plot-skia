/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Path
import org.jetbrains.skia.Rect

internal class Path : Figure() {
    var skiaPath: Path? by visualProp(null)

    override fun doDraw(canvas: Canvas) {
        if (skiaPath == null) {
            return
        }

        fillPaint?.let { canvas.drawPath(skiaPath!!, it) }
        strokePaint?.let { canvas.drawPath(skiaPath!!, it) }
    }

    override val localBounds: Rect
        get() {
            val path = skiaPath ?: return Rect.Companion.makeWH(0.0f, 0.0f)
            return (strokePaint?.getFillPath(path) ?: path).bounds
        }
}