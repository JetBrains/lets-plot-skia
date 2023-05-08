package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import android.widget.RelativeLayout.LayoutParams
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot

internal class FigureToSkia(
    private val buildInfo: FigureBuildInfo,
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
            is CompositeFigureSvgRoot -> ctx.processCompositeFigure(svgRoot)
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
            return SvgView(this, skiaWidget)
        }
    }

    private fun Context.processCompositeFigure(
        svgRoot: CompositeFigureSvgRoot,
    ): View {
        svgRoot.ensureContentBuilt()

        val rootPanel = RelativeLayout(this)

        fun toLayoutParams(from: DoubleRectangle): LayoutParams =
            LayoutParams(
                dp(from.dimension.x.toInt()),
                dp(from.dimension.y.toInt())
            ).apply {
                leftMargin = dp(from.origin.x.toInt())
                topMargin = dp(from.origin.y.toInt())
            }

        val skiaWidget = androidSkiaWidget(svgRoot.svg)
        val rootComponent = SvgView(this, skiaWidget)
        rootPanel.addView(rootComponent, toLayoutParams(svgRoot.bounds))


        //
        // Sub-plots
        //

        val elementJComponents = ArrayList<Pair<View, LayoutParams>>()
        for (element in svgRoot.elements) {
            if (element is PlotSvgRoot) {
                val comp = processPlotFigure(element)
                elementJComponents.add(comp to toLayoutParams(element.bounds))
            } else {
                val comp = processCompositeFigure(element as CompositeFigureSvgRoot)
                elementJComponents.add(comp to toLayoutParams(element.bounds))
            }
        }


        elementJComponents.forEach { (comp, layoutParams) ->
            // FIXME: only one tooltip should be visible among all subplots. Try to follow this recommendation:
//            rootJPanel.add(it)   // Do not!!!
            // Do not add everything to root panel.
            // Instead, build components tree: rootPanel -> rootComp -> [subComp->[subSubComp,...], ...].

            rootPanel.addView(comp, layoutParams)
        }

        return rootPanel
    }
}

private fun Context.dp(v: Number) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics).toInt()
