package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.view.View
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot

internal class FigureToSkia(
    private val buildInfo: FigureBuildInfo,
    private val plotSize: DoubleVector? = null,
    private val plotMaxWidth: Double? = null,
) {
    fun eval(ctx: Context): View {
        val buildInfo = buildInfo.layoutedByOuterSize()

        // TODO: livemap
        //buildInfo.injectLiveMapProvider { tiles: List<List<GeomLayer>>, spec: Map<String, Any> ->
        //    val cursorServiceConfig = CursorServiceConfig()
        //    LiveMapProviderUtil.injectLiveMapProvider(tiles, spec, cursorServiceConfig)
        //    cursorServiceConfig
        //}


        return when (val svgRoot = buildInfo.createSvgRoot()) {
            is CompositeFigureSvgRoot -> ctx.processCompositeFigure(origin = null, svgRoot)
            is PlotSvgRoot -> ctx.processPlotFigure(svgRoot)
            else -> error("Unsupported figure: ${svgRoot::class.simpleName}")
        }
    }

    private fun Context.processPlotFigure(svgRoot: PlotSvgRoot): View {
        if (svgRoot.isLiveMap) {
            error("LiveMap is not supported")
        } else {
            val plotContainer = PlotContainer(svgRoot)
            val skiaWidget = androidSkiaWidget(plotContainer.svg)
            skiaWidget.setMouseEventListener { s, e -> plotContainer.mouseEventPeer.dispatch(s, e) }
            return SkiaWidgetView(this, skiaWidget, plotSize, null)
        }
    }

    private fun Context.processCompositeFigure(
        origin: DoubleVector?,
        svgRoot: CompositeFigureSvgRoot,
    ): View {
        error("Not implemented")
    }
}
