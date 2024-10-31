/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.skia.Color
import org.jetbrains.skia.Color4f
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PathEffect.Companion.makeDash

internal abstract class Figure : Element() {
    var stroke: Color4f? by visualProp(null)
    var strokeWidth: Float by visualProp(1f)
    var strokeOpacity: Float by visualProp(1f)
    var strokeDashArray: List<Float>? by visualProp(null)
    var strokeMiter: Float? by visualProp(null) // not mandatory, default works fine

    var fill: Color4f? by visualProp(Color4f(Color.BLACK))
    var fillOpacity: Float by visualProp(1f)

    val fillPaint: Paint? by computedProp(Figure::fill, Figure::fillOpacity, managed = true) {
        return@computedProp fillPaint(fill, fillOpacity)
    }

    val strokePaint: Paint? by computedProp(
        Figure::stroke,
        Figure::strokeWidth,
        Figure::strokeDashArray,
        Figure::strokeOpacity,
        Figure::strokeMiter,
        managed = true
    ) {
        return@computedProp strokePaint(stroke, strokeWidth, strokeOpacity, strokeDashArray, strokeMiter)
    }

    protected fun strokePaint(
        stroke: Color4f? = null,
        strokeWidth: Float = 1f,
        strokeOpacity: Float = 1f,
        strokeDashArray: List<Float>? = null,
        strokeMiter: Float? = null // not mandatory, default works fine
    ) : Paint? {
        if (stroke == null) return null
        if (strokeOpacity == 0f) return null

        if (strokeWidth == 0f) {
            // Handle zero width manually, because Skia threatens 0 as "hairline" width, i.e. 1 pixel.
            // Source: https://api.skia.org/classSkPaint.html#af08c5bc138e981a4e39ad1f9b165c32c
            return null
        }

        val paint = Paint()
        paint.setStroke(true)
        paint.color4f = stroke.withA(strokeOpacity)
        paint.strokeWidth = strokeWidth
        strokeMiter?.let { paint.strokeMiter = it }
        strokeDashArray?.let { paint.pathEffect = makeDash(it.toFloatArray(), 0.0f) }
        return paint
    }

    protected fun fillPaint(fill: Color4f? = null, fillOpacity: Float = 1f): Paint? {
        if (fill == null) return null

        return Paint().also { paint ->
            paint.color4f = fill.withA(fillOpacity)
        }
    }
}