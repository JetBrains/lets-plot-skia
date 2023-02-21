package jetbrains.datalore.vis.svgMapper.skia

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot
import javax.swing.JComponent
import javax.swing.SwingUtilities

class FigureToSkia(
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
            is CompositeFigureSvgRoot -> processCompositeFigure(origin = null, svgRoot)
            is PlotSvgRoot -> processPlotFigure(svgRoot)
            else -> error("Unsupported figure: ${svgRoot::class.simpleName}")
        }
    }

    private fun processPlotFigure(svgRoot: PlotSvgRoot): JComponent {
        if (svgRoot.isLiveMap) {
            error("LiveMap is not supported")
        } else {
            val plotContainer = PlotContainer(svgRoot)
            val skiaWidget = swingSkiaWidget(svgRoot.svg)
            skiaWidget.setMouseEventListener { s, e ->
                plotContainer.mouseEventPeer.dispatch(s, e)
                SwingUtilities.invokeLater { skiaWidget.nativeLayer.needRedraw() }
            }
            return SkiaWidgetPanel(skiaWidget)
        }
    }

    private fun processCompositeFigure(
        origin: DoubleVector?,
        svgRoot: CompositeFigureSvgRoot,
    ): JComponent {
        error("Not implemented")
    }
}
