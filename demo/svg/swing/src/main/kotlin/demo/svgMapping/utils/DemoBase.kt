package demo.svgMapping.utils

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.vis.svg.SvgColors
import jetbrains.datalore.vis.svg.SvgCssResource
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.SvgSvgElement

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