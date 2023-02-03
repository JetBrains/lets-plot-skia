/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia.mapper.drawing

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Path
import org.jetbrains.skia.Rect

internal class Path: Figure() {
    var skiaPath: Path? by visualProp(null)

    override fun doDraw(canvas: Canvas) {
        if (skiaPath == null) {
            return
        }

        fillPaint?.let { canvas.drawPath(skiaPath!!, it) }
        strokePaint?.let { canvas.drawPath(skiaPath!!, it) }

    }

    override fun doGetBounds(): Rect {
        return skiaPath?.bounds?.offset(absoluteOffsetX, absoluteOffsetY) ?: Rect.Companion.makeWH(0.0f, 0.0f)
    }
}