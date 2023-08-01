/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.svg.mapper.attr

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathData
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.skia.shape.Path
import org.jetbrains.skia.Path.Companion.makeFromSVGString

internal object SvgPathAttrMapping : SvgShapeMapping<Path>() {
    override fun setAttribute(target: Path, name: String, value: Any?) {
        when (name) {
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

            else -> super.setAttribute(target, name, value)
        }
    }
}