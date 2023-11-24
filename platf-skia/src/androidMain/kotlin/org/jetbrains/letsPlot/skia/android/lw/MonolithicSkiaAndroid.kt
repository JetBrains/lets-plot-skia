/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.android.lw

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.PlotContainer
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgRoot
import org.jetbrains.letsPlot.core.plot.builder.subPlots.CompositeFigureSvgRoot
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.skia.android.CompositeFigureEventDispatcher
import org.jetbrains.letsPlot.skia.android.view.SvgPanel
import org.jetbrains.letsPlot.skia.view.SkikoViewEventDispatcher

/**
 * "lightweight" - no android Views are created here.
 */
object MonolithicSkiaAndroid {
    fun buildPlotFromProcessedSpecs(
        dest: SvgPanel,
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: (List<String>) -> Unit
    ): Registration {
        val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(plotSpec, plotSize)
        if (buildResult is MonolithicCommon.PlotsBuildResult.Error) {
            dest.svg = createErrorSvgText(buildResult.error)
            dest.eventDispatcher = null
            return Registration.EMPTY
        }

        val reg = CompositeRegistration()

        val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
        val computationMessages = success.buildInfos.flatMap(FigureBuildInfo::computationMessages)
        computationMessagesHandler(computationMessages)

        require(success.buildInfos.size == 1) { "GGBunch is not supported." }

        val buildInfo = success.buildInfos.single()
        val svgRoot = buildInfo.layoutedByOuterSize().createSvgRoot()
        val topSvgSvg: SvgSvgElement = svgRoot.svg

        val containerCleanup = CompositeRegistration()
        val dispatcher = when (svgRoot) {
            is CompositeFigureSvgRoot -> processCompositeFigure(svgRoot, topSvgSvg, containerCleanup)
            is PlotSvgRoot -> processPlotFigure(svgRoot, containerCleanup)
            else -> error("Unexpected root figure type: ${svgRoot::class.simpleName}")
        }

        dest.svg = topSvgSvg
        dest.eventDispatcher = dispatcher
        return reg
    }

    private fun processPlotFigure(
        svgRoot: PlotSvgRoot,
        containerCleanup: CompositeRegistration,
        parentEventDispatcher: CompositeFigureEventDispatcher? = null
    ): CompositeFigureEventDispatcher {
        val plotContainer = PlotContainer(svgRoot)
        containerCleanup.add(Registration.from(plotContainer))

        val panelDispatcher = object : SkikoViewEventDispatcher {
            override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
                plotContainer.mouseEventPeer.dispatch(kind, e)
            }
        }

        val dispatcher = parentEventDispatcher ?: CompositeFigureEventDispatcher()
        dispatcher.addEventDispatcher(svgRoot.bounds, panelDispatcher)
        return dispatcher
    }

    private fun processCompositeFigure(
        svgRoot: CompositeFigureSvgRoot,
        topSvgSvg: SvgSvgElement,
        containerCleanup: CompositeRegistration,
        origin: DoubleVector = DoubleVector.ZERO,
        parentEventDispatcher: CompositeFigureEventDispatcher? = null
    ): CompositeFigureEventDispatcher {
        svgRoot.ensureContentBuilt()

        val dispatcher = CompositeFigureEventDispatcher()
        parentEventDispatcher?.addEventDispatcher(svgRoot.bounds, dispatcher)

        // Sub-figures

        for (element in svgRoot.elements) {
            val elementOrigin = element.bounds.origin.add(origin)

            val elementSvg = element.svg
            elementSvg.x().set(elementOrigin.x)
            elementSvg.y().set(elementOrigin.y)

            topSvgSvg.children().add(elementSvg)

            when (element) {
                is CompositeFigureSvgRoot -> processCompositeFigure(
                    element,
                    topSvgSvg,
                    containerCleanup,
                    elementOrigin,
                    dispatcher
                )

                is PlotSvgRoot -> processPlotFigure(element, containerCleanup, dispatcher)
            }
        }
        return dispatcher
    }

    private fun createErrorSvgText(s: String): SvgSvgElement {
        return SvgSvgElement().apply {
            children().add(
                SvgTextElement(s)
            )
        }
    }
}
