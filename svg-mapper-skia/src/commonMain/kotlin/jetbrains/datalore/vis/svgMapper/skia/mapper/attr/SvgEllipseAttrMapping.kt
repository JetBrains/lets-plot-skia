/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia.mapper.attr

import jetbrains.datalore.vis.svg.SvgEllipseElement
import jetbrains.datalore.vis.svgMapper.skia.mapper.drawing.Ellipse

internal object SvgEllipseAttrMapping : SvgShapeMapping<Ellipse>() {
    override fun setAttribute(target: Ellipse, name: String, value: Any?) {
        when (name) {
            SvgEllipseElement.CX.name -> target.centerX = value?.asFloat ?: 0.0f
            SvgEllipseElement.CY.name -> target.centerY = value?.asFloat ?: 0.0f
            SvgEllipseElement.RX.name -> target.radiusX = value?.asFloat ?: 0.0f
            SvgEllipseElement.RY.name -> target.radiusY = value?.asFloat ?: 0.0f
            else -> super.setAttribute(target, name, value)
        }
    }
}