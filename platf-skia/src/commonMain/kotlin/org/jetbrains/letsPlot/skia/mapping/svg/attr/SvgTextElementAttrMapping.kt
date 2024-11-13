/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.mapping.svg.attr

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_TEXT_DY_CENTER
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_TEXT_DY_TOP
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.skia.mapping.svg.SvgUtils.toColor
import org.jetbrains.letsPlot.skia.shape.Text
import org.jetbrains.letsPlot.skia.shape.TextBlock

internal object SvgTextElementAttrMapping : SvgAttrMapping<Text>() {
    override fun setAttribute(target: Text, name: String, value: Any?) {
        when (name) {
            "font-size" -> target.fontSize = value?.asPxSize ?: Text.DEFAULT_FONT_SIZE
            "font-family" -> target.fontFamily = value?.asFontFamily ?: Text.DEFAULT_FONT_FAMILY
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

    private fun setStyle(style: String, target: TextBlock) {
        style
            .split(";")
            .flatMap { it.split(":") }
            .windowed(2, 2)
            .forEach { (attr, value) -> setAttribute(target, attr, value) }
    }

    fun setAttribute(target: TextBlock, name: String, value: Any?) {
        when (name) {
            SvgConstants.SVG_STYLE_ATTRIBUTE -> setStyle(value as? String ?: "", target)

            "font-size" -> target.fontSize = value?.asPxSize ?: Text.DEFAULT_FONT_SIZE
            "font-family" -> target.fontFamily = value?.asFontFamily ?: Text.DEFAULT_FONT_FAMILY
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

            SvgShape.FILL.name -> target.fill = toColor(value)
            SvgShape.FILL_OPACITY.name -> target.fillOpacity = value!!.asFloat
            SvgShape.STROKE.name -> target.stroke = toColor(value)
            SvgShape.STROKE_OPACITY.name -> target.strokeOpacity = value!!.asFloat
            SvgShape.STROKE_WIDTH.name -> target.strokeWidth = value!!.asFloat
            SvgConstants.SVG_STROKE_DASHARRAY_ATTRIBUTE -> {
                val strokeDashArray = (value as String).split(",").map(String::toFloat)
                target.strokeDashArray = strokeDashArray
            }
            else -> SvgAttrMapping.setAttribute(target, name, value)
        }
    }
}