package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.vis.svg.SvgSvgElement
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoGestureEventKind

fun Context.svgView(svg: SvgSvgElement): View {
    val skiaWidget = androidSkiaWidget(svg)
    return SkiaWidgetView(this, skiaWidget, null, null)
}

fun Context.plotView(
    plotSpec: MutableMap<String, Any>,
    plotSize: DoubleVector? = null,
    plotMaxWidth: Double? = null,
): View {
    val messages = mutableListOf<String>()
    val plots = MonolithicSkia.buildPlotFromProcessedSpecs(
        plotSpec,
        plotSize,
        plotMaxWidth,
        messages::addAll
    ).getOrThrow()

    val container = LinearLayout(this)

    plots.forEach { plotBuildInfo ->
        val plot = plotBuildInfo.plotAssembler.createPlot()
        val plotContainer = PlotContainer(plot, plotBuildInfo.size)

        plotContainer.ensureContentBuilt()
        val skiaWidget = androidSkiaWidget(plotContainer.svg)
        skiaWidget.setMouseEventListener { s, e -> plotContainer.mouseEventPeer.dispatch(s, e) }

        container.addView(SkiaWidgetView(this, skiaWidget, plotSize, null))
    }
    return container
}

private fun androidSkiaWidget(svg: SvgSvgElement): SkiaWidget {
    return SkiaWidget(svg, SkiaLayer()) { skiaLayer, skikoView ->
        skiaLayer.gesturesToListen = arrayOf(
            SkikoGestureEventKind.PAN,
            SkikoGestureEventKind.DOUBLETAP,
            SkikoGestureEventKind.TAP,
            SkikoGestureEventKind.LONGPRESS
        )
        skiaLayer.skikoView = skikoView
    }
}
