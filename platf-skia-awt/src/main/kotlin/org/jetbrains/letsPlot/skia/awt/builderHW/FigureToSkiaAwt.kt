/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.awt.builderHW

import org.jetbrains.letsPlot.awt.plot.DisposableJPanel
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.DisposingHub
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.PlotContainer
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgRoot
import org.jetbrains.letsPlot.core.plot.builder.subPlots.CompositeFigureSvgRoot
import org.jetbrains.letsPlot.skia.awt.view.SvgPanel
import org.jetbrains.letsPlot.skia.builderLW.CompositeFigureEventDispatcher
import org.jetbrains.letsPlot.skia.view.SkikoViewEventDispatcher
import javax.swing.JComponent

internal class FigureToSkiaAwt(
    private val buildInfo: FigureBuildInfo,
) {
    fun eval(): JComponent {
        val buildInfo = buildInfo.layoutedByOuterSize()

        // TODO: livemap
        //buildInfo.injectLiveMapProvider { tiles: List<List<GeomLayer>>, spec: Map<String, Any> ->
        //    val cursorServiceConfig = CursorServiceConfig()
        //    LiveMapProviderUtil.injectLiveMapProvider(tiles, spec, cursorServiceConfig)
        //    cursorServiceConfig
        //}

        return when (val svgRoot = buildInfo.createSvgRoot()) {
            is CompositeFigureSvgRoot -> processCompositeFigure(svgRoot, bounds = null, parentEventDispatcher = null)
            is PlotSvgRoot -> processPlotFigure(svgRoot, bounds = null, parentEventDispatcher = null)
            else -> error("Unsupported figure: ${svgRoot::class.simpleName}")
        }
    }

    private fun processCompositeFigure(
        svgRoot: CompositeFigureSvgRoot,
        bounds: Rectangle?,  // null -> root figure.
        parentEventDispatcher: CompositeFigureEventDispatcher? // null -> root figure.
    ): JComponent {

        svgRoot.ensureContentBuilt()

        // JPanel
        val rootJPanel = DisposableJPanel(null)
        rootJPanel.registerDisposable(
            object : Disposable {
                override fun dispose() {
                    svgRoot.clearContent()
                }
            }
        )

        rootJPanel.border = null
        rootJPanel.isOpaque = false
        bounds?.let {
            rootJPanel.bounds = toAwtRect(bounds)
        }

        //        val compositeEventDispatcher = CompositeFigureEventDispatcher()
        // For "gggrid" in Swing:
        // Child SvgPanels (SkikoView-s) do not receive Skiko mouse events.
        // So we have to receive events in the root SkikoView and dispatch them to child Skiko View-s.
        //
        // See also a note in org.jetbrains.letsPlot.skia.awt.view.SvgPanel
        val compositeEventDispatcher = CompositeFigureEventDispatcher().also {
            parentEventDispatcher?.addEventDispatcher(bounds!!, it)
        }

        val componentBounds = toAwtRect(
            DoubleRectangle(DoubleVector.ZERO, svgRoot.bounds.dimension)
        )
        val rootFigureDim = componentBounds.size
        rootJPanel.preferredSize = rootFigureDim
        rootJPanel.minimumSize = rootFigureDim
        rootJPanel.maximumSize = rootFigureDim

        val rootJComponent = SvgPanel(svgRoot.svg, compositeEventDispatcher)
        rootJComponent.bounds = componentBounds
        rootJPanel.add(rootJComponent)

        //
        // Sub-plots
        //

        for (element in svgRoot.elements) {
            val elementBounds = fromDoubleRect(element.bounds)
            val elementComponent = if (element is PlotSvgRoot) {
                processPlotFigure(element, elementBounds, compositeEventDispatcher)
            } else {
                processCompositeFigure(element as CompositeFigureSvgRoot, elementBounds, compositeEventDispatcher)
            }

//            rootJPanel.add(it)   // Do not!!!

            // Do not add everything to root panel.
            // Instead, build components tree: rootPanel -> rootComp -> [subComp->[subSubComp,...], ...].
            // Otherwise mouse events will not be handled properly.
            rootJComponent.add(elementComponent)
            rootJComponent.registerDisposable(elementComponent as Disposable)
        }

        return rootJPanel
    }


    private fun processPlotFigure(
        svgRoot: PlotSvgRoot,
        bounds: Rectangle?,  // null -> root figure
        parentEventDispatcher: CompositeFigureEventDispatcher?
    ): JComponent {
        if (svgRoot.isLiveMap) {
            error("LiveMap is not supported")
        }

        val plotContainer = PlotContainer(svgRoot)
        return buildSinglePlotComponent(plotContainer, bounds, parentEventDispatcher)
    }


    companion object {
        private fun buildSinglePlotComponent(
            plotContainer: PlotContainer,
            bounds: Rectangle?,
            parentEventDispatcher: CompositeFigureEventDispatcher?
        ): JComponent {
            val svg = plotContainer.svg


            val plotComponent = SvgPanel(
                svg = svg,
                eventDispatcher = object : SkikoViewEventDispatcher {
                    override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
                        plotContainer.mouseEventPeer.dispatch(kind, e)
                    }
                })

            parentEventDispatcher?.addEventDispatcher(
                bounds = bounds!!,
                eventDispatcher = plotComponent.eventDispatcher
                    ?: throw IllegalStateException("No SkikoViewEventDispatcher.")
            )

            (plotComponent as DisposingHub).registerDisposable(plotContainer)
            bounds?.let {
                plotComponent.bounds = toAwtRect(it)
            }

            return plotComponent
        }
    }
}
