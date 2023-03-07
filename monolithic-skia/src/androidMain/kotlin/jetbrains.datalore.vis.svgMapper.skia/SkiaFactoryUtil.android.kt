package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.view.View
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.vis.svg.SvgSvgElement
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoGestureEventKind

fun Context.svgView(svg: SvgSvgElement): View {
    return SkiaWidgetView(this, androidSkiaWidget(svg))
}

fun Context.plotView(
    rawPlotSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean = true,
    computationMessagesHandler: ((List<String>) -> Unit) = {}
): View {
    val processedSpec = MonolithicCommon.processRawSpecs(rawPlotSpec, frontendOnly = false)
    return PlotView(this, processedSpec, preserveAspectRatio, computationMessagesHandler)
}

internal fun androidSkiaWidget(svg: SvgSvgElement): SkiaWidget {
    return SkiaWidget(svg, SkiaLayer()) { skiaLayer, skikoView ->
        skiaLayer.gesturesToListen = arrayOf(
            SkikoGestureEventKind.PAN,
            SkikoGestureEventKind.DOUBLETAP,
            SkikoGestureEventKind.TAP,
            SkikoGestureEventKind.LONGPRESS
        )
        skiaLayer.skikoView = skikoView
        skiaLayer.needRedraw()
    }
}
