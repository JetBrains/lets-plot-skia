/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.mapping.svg.attr

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGraphicsElement.Companion.POINTER_EVENTS
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathData
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.skia.shape.Path
import org.jetbrains.skia.Path.Companion.makeFromSVGString

internal object SvgPathAttrMapping : SvgShapeMapping<Path>() {
    override fun setAttribute(target: Path, name: String, value: Any?) {
        when (name) {
            SvgPathElement.STROKE_MITER_LIMIT.name -> target.strokeMiter = value?.asFloat

            SvgPathElement.FILL_RULE.name -> {
                val fillRule = when (value) {
                    SvgPathElement.FillRule.NON_ZERO -> org.jetbrains.skia.PathFillMode.WINDING
                    SvgPathElement.FillRule.EVEN_ODD -> org.jetbrains.skia.PathFillMode.EVEN_ODD
                    null -> null
                    else -> throw IllegalArgumentException("Unknown fill-rule: $value")
                }

                target.fillRule = fillRule
            }

            SvgPathElement.D.name -> {
                // Can be string (slim path) or SvgPathData
                val pathStr = when (value) {
                    is String -> value
                    is SvgPathData -> value.toString()
                    null -> throw IllegalArgumentException("Undefined `path data`")
                    else -> throw IllegalArgumentException("Unexpected `path data` type: ${value::class.simpleName}")
                }

                target.skiaPath = makeFromSVGString(pathStr)
            }

            POINTER_EVENTS.name -> {} // target.pointerEvents = value as String

            else -> super.setAttribute(target, name, value)
        }
    }
}