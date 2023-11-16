/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.awt

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.PlotContainer
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgRoot
import org.jetbrains.letsPlot.core.plot.builder.subPlots.CompositeFigureSvgRoot
import org.jetbrains.letsPlot.core.spec.FailureHandler
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.skia.awt.view.SvgPanel
import org.jetbrains.letsPlot.skia.view.SkikoViewEventDispatcher
import javax.swing.JComponent
import javax.swing.JTextArea

object MonolithicSkiaAwt {
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
        return containerCleanup
    }

    private fun processPlotFigure(svgRoot: PlotSvgRoot, containerCleanup: CompositeRegistration, parentEventDispatcher: CompositeFigureEventDispatcher? = null): CompositeFigureEventDispatcher {
        val plotContainer = PlotContainer(svgRoot)
        containerCleanup.add(Registration.from(plotContainer))

        val panelDispatcher = object : SkikoViewEventDispatcher {
            override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
                plotContainer.mouseEventPeer.dispatch(kind, e)
            }
        }

        val dispatcher = parentEventDispatcher ?: CompositeFigureEventDispatcher()
        dispatcher.addEventDispatcher(toAwtRect(svgRoot.bounds), panelDispatcher)
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
        parentEventDispatcher?.addEventDispatcher(toAwtRect(svgRoot.bounds), dispatcher)

        // Sub-figures

        for (element in svgRoot.elements) {
            val elementOrigin = element.bounds.origin.add(origin)

            val elementSvg = element.svg
            elementSvg.x().set(elementOrigin.x)
            elementSvg.y().set(elementOrigin.y)

            when (element) {
                is CompositeFigureSvgRoot -> processCompositeFigure(element, topSvgSvg, containerCleanup, elementOrigin, dispatcher)
                is PlotSvgRoot -> processPlotFigure(element, containerCleanup, dispatcher)
            }

            topSvgSvg.children().add(elementSvg)
        }
        return dispatcher
    }

    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return try {
            @Suppress("NAME_SHADOWING")
            val plotSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)
            buildPlotFromProcessedSpecs(plotSpec, plotSize, computationMessagesHandler)
        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    fun buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: (List<String>) -> Unit
    ): JComponent {
        val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
            plotSpec,
            plotSize,
            plotMaxWidth = null,
            plotPreferredWidth = null
        )

        if (buildResult.isError) {
            val errorMessage =
                (buildResult as MonolithicCommon.PlotsBuildResult.Error).error
            return createErrorLabel(errorMessage)
        }

        val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
        val computationMessages = success.buildInfos.flatMap { it.computationMessages }
        computationMessagesHandler(computationMessages)

        return if (success.buildInfos.size == 1) {
            val buildInfo = success.buildInfos.single()
            FigureToSkiaAwt(buildInfo).eval()
        } else {
            throw IllegalArgumentException("GGBunch is not supported.")
        }
    }

    private fun handleException(e: RuntimeException): JComponent {
        val failureInfo = FailureHandler.failureInfo(e)
        if (failureInfo.isInternalError) {
            println(e)
        }
        return createErrorLabel(failureInfo.message)
    }

    private fun createErrorLabel(s: String): JComponent {
        return JTextArea(s)
    }

    private fun createErrorSvgText(s: String): SvgSvgElement {
        return SvgSvgElement().apply {
            children().add(
                SvgTextElement(s)
            )
        }
    }
}
