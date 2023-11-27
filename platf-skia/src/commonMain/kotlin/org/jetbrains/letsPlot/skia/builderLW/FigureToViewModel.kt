/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.builderLW

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.PlotContainer
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgRoot
import org.jetbrains.letsPlot.core.plot.builder.subPlots.CompositeFigureSvgRoot
import org.jetbrains.letsPlot.skia.view.SkikoViewEventDispatcher

internal object FigureToViewModel {
    fun eval(buildInfo: FigureBuildInfo): ViewModel {
        @Suppress("NAME_SHADOWING")
        val buildInfo = buildInfo.layoutedByOuterSize()
        return when (val svgRoot = buildInfo.createSvgRoot()) {
            is CompositeFigureSvgRoot -> processCompositeFigure(
                svgRoot = svgRoot,
                origin = DoubleVector.ZERO
            ).also {
                it.assembleAsRoot()
            }

            is PlotSvgRoot -> processPlotFigure(
                svgRoot = svgRoot,
                origin = DoubleVector.ZERO
            )

            else -> error("Unsupported figure: ${svgRoot::class.simpleName}")
        }
    }

    private fun processCompositeFigure(
        svgRoot: CompositeFigureSvgRoot,
        origin: DoubleVector,
    ): CompositeFigureModel {
        svgRoot.ensureContentBuilt()
        val figureSvgSvg = svgRoot.svg
        if (origin != DoubleVector.ZERO) {
            figureSvgSvg.x().set(origin.x)
            figureSvgSvg.y().set(origin.y)
        }

        val compositeFigureModel = CompositeFigureModel(
            svg = figureSvgSvg,
            bounds = toModelBounds(svgRoot.bounds),
        )

        // Sub-figures

        for (element in svgRoot.elements) {
            val elementOrigin = element.bounds.origin.add(origin)
            val elementModel = when (element) {
                is CompositeFigureSvgRoot -> processCompositeFigure(
                    svgRoot = element,
                    origin = elementOrigin,
                )

                is PlotSvgRoot -> processPlotFigure(
                    svgRoot = element,
                    origin = elementOrigin
                )

                else -> error("Unsupported figure: ${svgRoot::class.simpleName}")
            }

            compositeFigureModel.addChildFigure(elementModel)
        }

        return compositeFigureModel
    }

    private fun processPlotFigure(
        svgRoot: PlotSvgRoot,
        origin: DoubleVector,
    ): SinglePlotModel {

        val figureSvgSvg = svgRoot.svg
        if (origin != DoubleVector.ZERO) {
            figureSvgSvg.x().set(origin.x)
            figureSvgSvg.y().set(origin.y)
        }

        val plotContainer = PlotContainer(svgRoot)

        val panelDispatcher = object : SkikoViewEventDispatcher {
            override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
                plotContainer.mouseEventPeer.dispatch(kind, e)
            }
        }

        return SinglePlotModel(
            svg = figureSvgSvg,
            bounds = toModelBounds(svgRoot.bounds),
            eventDispatcher = panelDispatcher,
            registration = Registration.from(plotContainer)
        )
    }

    private fun toModelBounds(from: DoubleRectangle): Rectangle {
        return Rectangle(
            from.origin.x.toInt(),
            from.origin.y.toInt(),
            (from.dimension.x + 0.5).toInt(),
            (from.dimension.y + 0.5).toInt()
        )
    }
}
