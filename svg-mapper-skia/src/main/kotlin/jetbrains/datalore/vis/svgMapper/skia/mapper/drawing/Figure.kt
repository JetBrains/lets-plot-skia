/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia.mapper.drawing

import org.jetbrains.skia.Color
import org.jetbrains.skia.Color4f
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PathEffect.Companion.makeDash

internal abstract class Figure : Element() {
    var stroke: Color4f? by visualProp(null)
    var strokeWidth: Float by visualProp(1f)
    var strokeOpacity: Float by visualProp(1f)
    var strokeDashArray: List<Float>? by visualProp(null)

    var fill: Color4f? by visualProp(Color4f(Color.BLACK))
    var fillOpacity: Float by visualProp(1f)

    val fillPaint: Paint? by dependencyProp(Figure::fill, Figure::fillOpacity) {
        val fill = fill ?: return@dependencyProp null

        return@dependencyProp Paint().also { paint ->
            paint.color4f = fill.withA(fillOpacity)
        }
    }

    val strokePaint: Paint? by dependencyProp(Figure::stroke, Figure::strokeWidth, Figure::strokeDashArray) {
        val stroke = stroke ?: return@dependencyProp null

        if (strokeOpacity == 0f) return@dependencyProp null

        if (strokeWidth == 0f) {
            // Handle zero width manually, because Skia threatens 0 as "hairline" width, i.e. 1 pixel.
            // Source: https://api.skia.org/classSkPaint.html#af08c5bc138e981a4e39ad1f9b165c32c
            return@dependencyProp null
        }

        return@dependencyProp Paint().also { paint ->
            paint.setStroke(true)
            paint.color4f = stroke.withA(strokeOpacity)
            paint.strokeWidth = strokeWidth
            strokeDashArray?.let { paint.pathEffect = makeDash(it.toFloatArray(), 0.0f) }
        }
    }
}