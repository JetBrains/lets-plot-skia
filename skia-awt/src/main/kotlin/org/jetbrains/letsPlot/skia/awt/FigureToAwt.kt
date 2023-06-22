package org.jetbrains.letsPlot.skia.awt

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.awt.AwtEventUtil
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.DisposingHub
import jetbrains.datalore.plot.DisposableJPanel
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot
import org.jetbrains.letsPlot.skia.awt.view.SvgPanel
import org.jetbrains.letsPlot.skia.svg.view.SkikoViewEventDispatcher
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import javax.swing.JComponent

internal class FigureToAwt(
    private val buildInfo: FigureBuildInfo,
    private val isComposeDesktop: Boolean
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
        val compositeEventDispatcher = if (isComposeDesktop) {
            null
        } else {
            // For "gggrid" in Swing:
            // Child SvgPanels (SkikoView-s) do not receive Skiko mouse events.
            // So we have to receive events in the root SkikoView and dispatch them to child Skiko View-s.
            //
            // See also a note in org.jetbrains.letsPlot.skia.awt.view.SvgPanel
            CompositeFigureEventDispatcher().also {
                parentEventDispatcher?.addEventDispatcher(bounds!!, it)
            }
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

        val rootJComponent = SvgPanel(svgRoot.svg, isComposeDesktop, compositeEventDispatcher)
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
        return buildSinglePlotComponent(plotContainer, bounds, isComposeDesktop, parentEventDispatcher)
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
                        button = e.button!!,
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
            isComposeDesktop: Boolean,
            parentEventDispatcher: CompositeFigureEventDispatcher?
        ): JComponent {
            val svg = plotContainer.svg


            val plotComponent = SvgPanel(
                svg = svg,
                isComposeDesktop = isComposeDesktop,
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

            if (isComposeDesktop) {

                // In Compose-Desktop we have to receive awt Mouse events in the
                // parent component (here) and dispatch them to the child SkikoView.
                //
                // See also a note in org.jetbrains.letsPlot.skia.awt.view.SvgPanel

                plotComponent.addMouseMotionListener(object : MouseAdapter() {
                    override fun mouseMoved(e: java.awt.event.MouseEvent) {
                        super.mouseMoved(e)
                        plotComponent.eventDispatcher.dispatchMouseEvent(
                            MouseEventSpec.MOUSE_MOVED,
                            AwtEventUtil.translate(e)
                        )
                    }

                    override fun mouseDragged(e: java.awt.event.MouseEvent) {
                        super.mouseDragged(e)
                        plotComponent.eventDispatcher.dispatchMouseEvent(
                            MouseEventSpec.MOUSE_DRAGGED,
                            AwtEventUtil.translate(e)
                        )
                    }
                })

                plotComponent.addMouseListener(object : MouseAdapter() {
                    override fun mouseExited(e: java.awt.event.MouseEvent) {
                        super.mouseExited(e)
                        plotComponent.eventDispatcher.dispatchMouseEvent(
                            MouseEventSpec.MOUSE_LEFT,
                            AwtEventUtil.translate(e)
                        )
                    }

                    override fun mouseClicked(e: java.awt.event.MouseEvent) {
                        super.mouseClicked(e)
                        val event = if (e.clickCount % 2 == 1) {
                            MouseEventSpec.MOUSE_CLICKED
                        } else {
                            MouseEventSpec.MOUSE_DOUBLE_CLICKED
                        }

                        plotComponent.eventDispatcher.dispatchMouseEvent(event, AwtEventUtil.translate(e))
                    }

                    override fun mousePressed(e: java.awt.event.MouseEvent) {
                        super.mousePressed(e)
                        plotComponent.eventDispatcher.dispatchMouseEvent(
                            MouseEventSpec.MOUSE_PRESSED,
                            AwtEventUtil.translate(e)
                        )
                    }

                    override fun mouseReleased(e: java.awt.event.MouseEvent) {
                        super.mouseReleased(e)
                        plotComponent.eventDispatcher.dispatchMouseEvent(
                            MouseEventSpec.MOUSE_RELEASED,
                            AwtEventUtil.translate(e)
                        )
                    }

                    override fun mouseEntered(e: java.awt.event.MouseEvent) {
                        super.mouseEntered(e)
                        plotComponent.eventDispatcher.dispatchMouseEvent(
                            MouseEventSpec.MOUSE_ENTERED,
                            AwtEventUtil.translate(e)
                        )
                    }
                })
            }
            return plotComponent
        }
    }
}
