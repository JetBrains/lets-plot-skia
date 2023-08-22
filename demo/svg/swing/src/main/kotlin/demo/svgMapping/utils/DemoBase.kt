/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping.utils

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgCssResource
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

internal abstract class DemoBase(
    private val demoInnerSize: DoubleVector
) {
    protected open val cssStyle: String = Style.generateCSS(Style.default(), plotId = null, decorationLayerId = null)

    private val demoOuterSize = demoInnerSize.add(PADDING.mul(2.0))

    fun createSvgRoots(demoGroups: List<GroupComponent>): List<SvgSvgElement> {
        return demoGroups.map {
            it.moveTo(PADDING)
            val svgRoot = createSvgRoot()
            svgRoot.children().add(it.rootGroup)
            svgRoot
        }
    }

    private fun createSvgRoot(): SvgSvgElement {
        val svg = SvgSvgElement()
        svg.width().set(demoOuterSize.x)
        svg.height().set(demoOuterSize.y)
        svg.addClass(Style.PLOT_CONTAINER)

        svg.setStyle(object : SvgCssResource {
            override fun css(): String = cssStyle
        })

        val viewport = DoubleRectangle(PADDING, demoInnerSize)
        val viewportRect = SvgRectElement(viewport)
        viewportRect.stroke().set(SvgColors.LIGHT_BLUE)
        viewportRect.fill().set(SvgColors.NONE)
        svg.children().add(viewportRect)

        return svg
    }

    companion object {
        private val PADDING = DoubleVector(20.0, 20.0)
    }
}