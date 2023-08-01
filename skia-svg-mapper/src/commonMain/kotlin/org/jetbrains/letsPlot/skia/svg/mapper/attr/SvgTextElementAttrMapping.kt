/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.svg.mapper.attr

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_TEXT_DY_CENTER
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_TEXT_DY_TOP
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.skia.shape.Text

internal object SvgTextElementAttrMapping : SvgShapeMapping<Text>() {
    override fun setAttribute(target: Text, name: String, value: Any?) {
        when (name) {
            SvgTextElement.X.name -> target.x = value?.asFloat ?: 0.0f
            SvgTextElement.Y.name -> target.y = value?.asFloat ?: 0.0f
            SvgTextContent.TEXT_ANCHOR.name -> {
                val svgTextAnchor = value as String?
                when (svgTextAnchor) {
                    SvgConstants.SVG_TEXT_ANCHOR_END -> target.textAlignment = Text.HorizontalAlignment.RIGHT
                    SvgConstants.SVG_TEXT_ANCHOR_MIDDLE -> target.textAlignment = Text.HorizontalAlignment.CENTER
                    SvgConstants.SVG_TEXT_ANCHOR_START -> target.textAlignment = Text.HorizontalAlignment.LEFT
                    else -> println("Unknown alignment")
                }
            }

            SvgTextContent.TEXT_DY.name -> {
                when (value) {
                    SVG_TEXT_DY_TOP -> target.textOrigin = Text.VerticalAlignment.TOP
                    SVG_TEXT_DY_CENTER -> target.textOrigin = Text.VerticalAlignment.CENTER
                    else -> throw IllegalStateException("Unexpected text 'dy' value: $value")
                }
            }

            SvgTextContent.FILL.name,
            SvgTextContent.FILL_OPACITY.name,
            SvgTextContent.STROKE.name,
            SvgTextContent.STROKE_OPACITY.name,
            SvgTextContent.STROKE_WIDTH.name -> super.setAttribute(target, name, value)

            else -> super.setAttribute(target, name, value)
        }
    }

}