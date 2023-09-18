/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.mapping.svg.attr

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape
import org.jetbrains.letsPlot.skia.shape.Figure
import org.jetbrains.letsPlot.skia.shape.asSkiaColor
import org.jetbrains.letsPlot.skia.shape.namedColors
import org.jetbrains.skia.Color4f

internal abstract class SvgShapeMapping<TargetT : Figure> : SvgAttrMapping<TargetT>() {
    override fun setAttribute(target: TargetT, name: String, value: Any?) {
        when (name) {
            SvgShape.FILL.name -> target.fill = toColor(value)
            SvgShape.FILL_OPACITY.name -> target.fillOpacity = value!!.asFloat
            SvgShape.STROKE.name -> target.stroke = toColor(value)
            SvgShape.STROKE_OPACITY.name -> target.strokeOpacity = value!!.asFloat
            SvgShape.STROKE_WIDTH.name -> target.strokeWidth = value!!.asFloat
            SvgConstants.SVG_STROKE_DASHARRAY_ATTRIBUTE -> {
                val strokeDashArray = (value as String).split(",").map(String::toFloat)
                target.strokeDashArray = strokeDashArray
            }

            else -> super.setAttribute(target, name, value)
        }
    }

    companion object {

        /**
         * value : the color name (string) or SvgColor (jetbrains.datalore.vis.svg)
         */
        private fun toColor(value: Any?): Color4f? {
            require(value != SvgColors.CURRENT_COLOR) { "currentColor is not supported" }

            return when (value) {
                null, SvgColors.NONE -> null
                else -> {
                    val colorString = value.toString().lowercase()
                    namedColors[colorString]
                        ?: Color.parseOrNull(colorString)
                        ?: error("Unsupported color value: $colorString")
                }
            }?.asSkiaColor
        }
    }
}
