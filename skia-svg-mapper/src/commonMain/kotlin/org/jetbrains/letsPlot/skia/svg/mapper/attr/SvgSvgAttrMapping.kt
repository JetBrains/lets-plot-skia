/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.svg.mapper.attr

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.skia.shape.Pane

internal object SvgSvgAttrMapping : SvgAttrMapping<Pane>() {

    override fun setAttribute(target: Pane, name: String, value: Any?) {
        when (name) {
            SvgSvgElement.X.name -> target.x = value?.asFloat ?: 0.0f
            SvgSvgElement.Y.name -> target.y = value?.asFloat ?: 0.0f
            SvgSvgElement.WIDTH.name -> target.width = value?.asFloat ?: 0.0f
            SvgSvgElement.HEIGHT.name -> target.height = value?.asFloat ?: 0.0f
//            SvgSvgElement.VIEW_BOX  ??
            else -> super.setAttribute(target, name, value)
        }
    }
}
