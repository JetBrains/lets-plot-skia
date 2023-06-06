package jetbrains.datalore.vis.svgMapper.skia

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.DisposingHub
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot
import org.jetbrains.letsPlot.skiko.SkikoViewEventDispatcher
import org.jetbrains.letsPlot.skiko.desktop.SvgPanelDesktop
import java.awt.Rectangle
import javax.swing.JComponent
import javax.swing.JPanel

class FigureToSkikoSwing(
    private val buildInfo: FigureBuildInfo
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
            is CompositeFigureSvgRoot -> processCompositeFigure(svgRoot)
            is PlotSvgRoot -> processPlotFigure(svgRoot)
            else -> error("Unsupported figure: ${svgRoot::class.simpleName}")
        }
    }

    private fun processCompositeFigure(
        svgRoot: CompositeFigureSvgRoot,
    ): JComponent {

        svgRoot.ensureContentBuilt()

        // JPanel
        val rootJPanel = JPanel(null)
        //rootJPanel.registerDisposable(
        //    object : Disposable {
        //        override fun dispose() {
        //            svgRoot.clearContent()
        //        }
        //    }
        //)

        rootJPanel.border = null
        rootJPanel.isOpaque = false

        fun toJBounds(from: DoubleRectangle): Rectangle {
            return Rectangle(
                from.origin.x.toInt(),
                from.origin.y.toInt(),
                from.dimension.x.toInt(),
                from.dimension.y.toInt()
            )
        }

//        val rootFigureBounds = toJBounds(svgRoot.bounds)
        val rootJComponentBounds = toJBounds(
            DoubleRectangle(DoubleVector.ZERO, svgRoot.bounds.dimension)
        )
        val rootFigureDim = rootJComponentBounds.size
        rootJPanel.preferredSize = rootFigureDim
        rootJPanel.minimumSize = rootFigureDim
        rootJPanel.maximumSize = rootFigureDim

        val skiaWidget = swingSkiaWidget(svgRoot.svg)
        val rootJComponent: JComponent = SvgPanel(skiaWidget)
        rootJComponent.layout = null // default SvgPanel layout breaks composite plots
        rootJComponent.bounds = rootJComponentBounds
        rootJPanel.add(rootJComponent)

        //
        // Sub-plots
        //

        val elementJComponents = ArrayList<JComponent>()
        for (element in svgRoot.elements) {
            if (element is PlotSvgRoot) {
                val comp = processPlotFigure(element)
                comp.bounds = toJBounds(element.bounds)
                elementJComponents.add(comp)
            } else {
                val comp = processCompositeFigure(element as CompositeFigureSvgRoot)
                comp.bounds = toJBounds(element.bounds)
                elementJComponents.add(comp)
            }
        }

        elementJComponents.forEach {
//            rootJPanel.add(it)   // Do not!!!

            // Do not add everything to root panel.
            // Instead, build components tree: rootPanel -> rootComp -> [subComp->[subSubComp,...], ...].
            // Otherwise mouse events will not be handled properly.
            rootJComponent.add(it)
        }

        return rootJPanel
    }


    private fun processPlotFigure(svgRoot: PlotSvgRoot): JComponent {
        if (svgRoot.isLiveMap) {
            error("LiveMap is not supported")
        }

        val plotContainer = PlotContainer(svgRoot)
        return buildSinglePlotComponent(plotContainer)
    }


    companion object {
        private fun buildSinglePlotComponent(
            plotContainer: PlotContainer,
        ): JComponent {
            val svg = plotContainer.svg

            val eventDispatcher = object : SkikoViewEventDispatcher {
                override fun dispatchMouseEvent(kind: MouseEventSpec, event: MouseEvent) {
                    plotContainer.mouseEventPeer.dispatch(kind, event)
                }
            }

            val plotComponent: JComponent = SvgPanelDesktop(svg, eventDispatcher)
            (plotComponent as DisposingHub).registerDisposable(plotContainer)
            return plotComponent
        }
    }
}
