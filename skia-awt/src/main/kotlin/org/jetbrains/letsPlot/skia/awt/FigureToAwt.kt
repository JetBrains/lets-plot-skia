package org.jetbrains.letsPlot.skia.awt

import org.jetbrains.letsPlot.awt.plot.DisposableJPanel
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.DisposingHub
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.jetbrains.letsPlot.core.plot.builder.PlotContainer
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgRoot
import org.jetbrains.letsPlot.core.plot.builder.subPlots.CompositeFigureSvgRoot
import org.jetbrains.letsPlot.skia.awt.view.SvgPanel
import org.jetbrains.letsPlot.skia.svg.view.SkikoViewEventDispatcher
import java.awt.Point
import java.awt.Rectangle
import javax.swing.JComponent

internal class FigureToAwt(
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
            rootJPanel.bounds = bounds
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

        fun toAwtRect(from: DoubleRectangle): Rectangle {
            return Rectangle(
                from.origin.x.toInt(),
                from.origin.y.toInt(),
                (from.dimension.x + 0.5).toInt(),
                (from.dimension.y + 0.5).toInt()
            )
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
            val awtBounds = toAwtRect(element.bounds)
            val elementComponent = if (element is PlotSvgRoot) {
                processPlotFigure(element, awtBounds, compositeEventDispatcher)
            } else {
                processCompositeFigure(element as CompositeFigureSvgRoot, awtBounds, compositeEventDispatcher)
            }

//            rootJPanel.add(it)   // Do not!!!

            // Do not add everything to root panel.
            // Instead, build components tree: rootPanel -> rootComp -> [subComp->[subSubComp,...], ...].
            // Otherwise mouse events will not be handled properly.
            rootJComponent.add(elementComponent)
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

    private class CompositeFigureEventDispatcher() : SkikoViewEventDispatcher {
        private val dispatchers = LinkedHashMap<Rectangle, SkikoViewEventDispatcher>()

        fun addEventDispatcher(bounds: Rectangle, eventDispatcher: SkikoViewEventDispatcher) {
            dispatchers[bounds] = eventDispatcher
        }

        override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
            val loc = Point(e.x, e.y)
            val target = dispatchers.keys.find { it.contains(loc) }
            if (target != null) {
                val dispatcher = dispatchers.getValue(target)
                dispatcher.dispatchMouseEvent(
                    kind,
                    MouseEvent(
                        v = Vector(loc.x - target.x, loc.y - target.y),
                        button = e.button,
                        modifiers = e.modifiers
                    )
                )
            }
        }
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
            )

            (plotComponent as DisposingHub).registerDisposable(plotContainer)
            bounds?.let {
                plotComponent.bounds = it
            }

            return plotComponent
        }
    }
}
