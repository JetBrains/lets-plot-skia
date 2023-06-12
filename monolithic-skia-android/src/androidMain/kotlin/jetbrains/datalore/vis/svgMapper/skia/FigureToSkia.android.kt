package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.RelativeLayout.LayoutParams
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.builder.PlotSvgRoot
import jetbrains.datalore.plot.builder.subPlots.CompositeFigureSvgRoot
import org.jetbrains.letsPlot.skiko.SkikoViewEventDispatcher
import org.jetbrains.letsPlot.skiko.android.SvgPanelAndroid

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
            return SvgPanelAndroid(
                context = this,
                svg = plotContainer.svg,
                eventDispatcher = object : SkikoViewEventDispatcher {
                    override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
                        plotContainer.mouseEventPeer.dispatch(kind, e)
                    }
                })
        }
    }

    private fun Context.processCompositeFigure(
        svgRoot: CompositeFigureSvgRoot,
    ): View {
        svgRoot.ensureContentBuilt()

        val viewGroup: ViewGroup = RelativeLayout(this)

        fun toLayoutParams(from: DoubleRectangle): LayoutParams =
            LayoutParams(
                dp(from.dimension.x.toInt()),
                dp(from.dimension.y.toInt())
            ).apply {
                leftMargin = dp(from.origin.x.toInt())
                topMargin = dp(from.origin.y.toInt())
            }

        val rootView = SvgPanelAndroid(
            context = this,
            svg = svgRoot.svg,
            eventDispatcher = null
        )
//            eventDispatcher = object : SkikoViewEventDispatcher {
//                override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
//                    plotContainer.mouseEventPeer.dispatch(kind, e)
//                }
//            })

        rootView.registerDisposable(object : Disposable {
            override fun dispose() {
                svgRoot.clearContent()
            }
        })

        viewGroup.addView(rootView, toLayoutParams(svgRoot.bounds))


        //
        // Sub-plots
        //

        for (element in svgRoot.elements) {
            val elementView = if (element is PlotSvgRoot) {
                processPlotFigure(element)
            } else {
                processCompositeFigure(element as CompositeFigureSvgRoot)
            }

            // FIXME: only one tooltip should be visible among all subplots. Try to follow this recommendation:
            viewGroup.addView(elementView, toLayoutParams(element.bounds))
        }

        return viewGroup
    }
}

private fun Context.dp(v: Number) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics).toInt()
