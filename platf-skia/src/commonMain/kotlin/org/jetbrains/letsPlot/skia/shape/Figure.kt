/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.skia.Color4f
import org.jetbrains.skia.Paint

internal abstract class Figure : Element() {
    var stroke: Color4f? by visualProp(null)
    var strokeWidth: Float by visualProp(1f)
    var strokeOpacity: Float by visualProp(1f)
    var strokeDashArray: List<Float>? by visualProp(null)
    var strokeMiter: Float? by visualProp(null) // not mandatory, default works fine

    var fill: Color4f? by visualProp(null)
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
}