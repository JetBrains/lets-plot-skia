/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.svg.mapper.attr

import jetbrains.datalore.vis.svg.SvgRectElement
import org.jetbrains.letsPlot.skia.shape.Rectangle

internal object SvgRectAttrMapping : SvgShapeMapping<Rectangle>() {
    override fun setAttribute(target: Rectangle, name: String, value: Any?) {
        when (name) {
            SvgRectElement.X.name -> target.x = value?.asFloat ?: 0.0f
            SvgRectElement.Y.name -> target.y = value?.asFloat ?: 0.0f
            SvgRectElement.WIDTH.name -> {
                if (!ignoredSizeValue(value)) {
                    target.width = value?.asFloat ?: 0.0f
                }
            }

            SvgRectElement.HEIGHT.name -> {
                if (!ignoredSizeValue(value)) {
                    target.height = value?.asFloat ?: 0.0f
                }
            }

            else -> super.setAttribute(target, name, value)
        }
    }

    private fun ignoredSizeValue(value: Any?): Boolean {
        // Do not fail on persentages, just ignore.
        return value is String && value.endsWith("%")
    }
}