/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia.mapper.drawing

import org.jetbrains.skia.Color4f
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PathEffect.Companion.makeDash

internal abstract class Figure : Element() {
    var fill: Color4f? by visualProp(null)
    var stroke: Color4f? by visualProp(null)
    var strokeWidth: Float? by visualProp(null)
    var strokeDashArray: List<Float>? by visualProp(null)

    val fillPaint: Paint? by dependencyProp(Figure::fill) {
        fill?.let { Paint().apply { color4f = it } }
    }

    val strokePaint: Paint? by dependencyProp(Figure::stroke, Figure::strokeWidth, Figure::strokeDashArray) {
        if (stroke == null && strokeWidth == null && strokeDashArray == null) {
            return@dependencyProp null
        }

        if (strokeWidth == 0f) {
            // Handle zero width manually, because Skia threatens 0 as "hairline" width, i.e. 1 pixel.
            // Source: https://api.skia.org/classSkPaint.html#af08c5bc138e981a4e39ad1f9b165c32c
            return@dependencyProp null
        }

        return@dependencyProp Paint().also { paint ->
            paint.setStroke(true)
            stroke?.let { paint.color4f = it }
            strokeWidth?.let { paint.strokeWidth = it }
            strokeDashArray?.let { paint.pathEffect = makeDash(it.toFloatArray(), 0.0f) }
        }
    }
}