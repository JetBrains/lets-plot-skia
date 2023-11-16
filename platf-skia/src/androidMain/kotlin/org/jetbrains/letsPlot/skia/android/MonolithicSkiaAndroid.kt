/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.android

import android.content.Context
import android.view.View
import android.widget.TextView
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
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
import org.jetbrains.letsPlot.skia.android.view.SvgPanel
import org.jetbrains.letsPlot.skia.view.SkikoViewEventDispatcher

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

            when (element) {
                is CompositeFigureSvgRoot -> processCompositeFigure(element, topSvgSvg, containerCleanup, elementOrigin, dispatcher)
                is PlotSvgRoot -> processPlotFigure(element, containerCleanup, dispatcher)
            }

            topSvgSvg.children().add(elementSvg)
        }
        return dispatcher
    }

    fun buildPlotFromRawSpecs(
        ctx: Context,
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): View {
        return try {
            val processedPlotSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)
            return buildPlotFromProcessedSpecs(ctx, processedPlotSpec, plotSize, computationMessagesHandler)
        } catch (e: RuntimeException) {
            ctx.handleException(e)
        }
    }

    fun buildPlotFromProcessedSpecs(
        ctx: Context,
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): View {
        return try {
            val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
                plotSpec,
                plotSize,
                plotMaxWidth = null,
                plotPreferredWidth = null
            )
            if (buildResult.isError) {
                val errorMessage = (buildResult as MonolithicCommon.PlotsBuildResult.Error).error
                return ctx.createErrorLabel(errorMessage)
            }

            val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
            val computationMessages = success.buildInfos.flatMap { it.computationMessages }
            computationMessagesHandler(computationMessages)
            return if (success.buildInfos.size == 1) {
                // a single plot
                val buildInfo = success.buildInfos[0]
                FigureToSkiaAndroid(buildInfo).eval(ctx)
            } else {
                // ggbunch
                error("GGBunch is not supported.")
            }

        } catch (e: RuntimeException) {
            ctx.handleException(e)
        }
    }

    private fun Context.handleException(e: RuntimeException): View {
        val failureInfo = FailureHandler.failureInfo(e)
        if (failureInfo.isInternalError) {
            println(e)
        }
        return createErrorLabel(failureInfo.message)
    }

    private fun Context.createErrorLabel(s: String): View {
        val label = TextView(this)
        label.text = s
        label.setTextColor(0x00FF0000)
        return label
    }

    private fun createErrorSvgText(s: String): SvgSvgElement {
        return SvgSvgElement().apply {
            children().add(
                SvgTextElement(s)
            )
        }
    }

}

internal class CompositeFigureEventDispatcher() : SkikoViewEventDispatcher {
    private val dispatchers = LinkedHashMap<Rectangle, SkikoViewEventDispatcher>()

    fun addEventDispatcher(bounds: DoubleRectangle, eventDispatcher: SkikoViewEventDispatcher) {
        val rect = Rectangle(
            bounds.origin.x.toInt(),
            bounds.origin.y.toInt(),
            bounds.dimension.x.toInt(),
            bounds.dimension.y.toInt()
        )
        dispatchers[rect] = eventDispatcher
    }

    override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
        val loc = Vector(e.x, e.y)
        val target = dispatchers.keys.find { it.contains(loc) }
        if (target != null) {
            val dispatcher = dispatchers.getValue(target)
            dispatcher.dispatchMouseEvent(
                kind,
                MouseEvent(
                    v = Vector(loc.x - target.origin.x, loc.y - target.origin.y),
                    button = e.button,
                    modifiers = e.modifiers
                )
            )
        }
    }
}