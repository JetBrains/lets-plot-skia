/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.mapping.svg.attr

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.skia.mapping.svg.SvgUtils.toColor
import org.jetbrains.letsPlot.skia.shape.TSpan
import org.jetbrains.letsPlot.skia.shape.Text

internal object SvgTSpanElementAttrMapping : SvgShapeMapping<TSpan>() {
    override fun setAttribute(target: TSpan, name: String, value: Any?) {
        when (name) {
            SvgShape.FILL.name -> target.fill = toColor(value)
            //SvgShape.FILL_OPACITY.name -> target.fillOpacity = value!!.asFloat
            SvgShape.STROKE.name -> target.stroke = toColor(value)
            //SvgShape.STROKE_OPACITY.name -> target.strokeOpacity = value!!.asFloat
            SvgShape.STROKE_WIDTH.name -> target.strokeWidth = value!!.asFloat
            "font-size" -> {
                require(value is String) { "font-size: only string value is supported" }
                target.fontScale = when {
                    "em" in value -> value.removeSuffix("em").toFloat()
                    "%" in value -> value.removeSuffix("%").toFloat() / 100.0f
                    else -> 1f
                }
            }
            "font-family" -> target.fontFamily = value?.asFontFamily ?: Text.DEFAULT_FONT_FAMILY

            "baseline-shift" -> target.baselineShift = when (value) {
                "sub" -> Text.BaselineShift.SUB
                "super" -> Text.BaselineShift.SUPER
                else -> error("Unexpected baseline-shift value: $value")
            }

            "dy" -> {
                require(value is String) { "dy: only string value is supported" }
                target.dy = when {
                    "em" in value -> value.removeSuffix("em").toFloat()
                    "%" in value -> value.removeSuffix("%").toFloat() / 100.0f
                    else -> 0f
                }
            }
            else -> super.setAttribute(target, name, value)
        }
    }

    fun setAttributes(target: Text.TextRun, tspan: SvgTSpanElement) {
        tspan.attributeKeys.forEach { key ->
            setAttribute(target, key.name, tspan.getAttribute(key).get())
        }
    }

    fun setAttributes(target: TSpan, tspan: SvgTSpanElement) {
        tspan.attributeKeys.forEach { key ->
            setAttribute(target, key.name, tspan.getAttribute(key).get())
        }
    }

    fun setAttribute(target: Text.TextRun, name: String, value: Any?) {
        when (name) {
            SvgShape.FILL.name -> target.fill = toColor(value)
            //SvgShape.FILL_OPACITY.name -> target.fillOpacity = value!!.asFloat
            SvgShape.STROKE.name -> target.stroke = toColor(value)
            //SvgShape.STROKE_OPACITY.name -> target.strokeOpacity = value!!.asFloat
            SvgShape.STROKE_WIDTH.name -> target.strokeWidth = value!!.asFloat
            "font-size" -> {
                require(value is String) { "font-size: only string value is supported" }
                target.fontScale = when {
                    "em" in value -> value.removeSuffix("em").toFloat()
                    "%" in value -> value.removeSuffix("%").toFloat() / 100.0f
                    else -> 1f
                }
            }

            "baseline-shift" -> target.baselineShift = when (value) {
                "sub" -> Text.BaselineShift.SUB
                "super" -> Text.BaselineShift.SUPER
                else -> error("Unexpected baseline-shift value: $value")
            }

            "dy" -> {
                require(value is String) { "dy: only string value is supported" }
                target.dy = when {
                    "em" in value -> value.removeSuffix("em").toFloat()
                    "%" in value -> value.removeSuffix("%").toFloat() / 100.0f
                    else -> 0f
                }
            }
        }
    }
}