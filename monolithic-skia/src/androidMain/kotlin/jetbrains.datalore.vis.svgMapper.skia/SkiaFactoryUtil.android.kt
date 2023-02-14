package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.view.View
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.skia.MonolithicAndroid.buildPlotFromRawSpecs
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoGestureEventKind

fun Context.svgView(svg: SvgSvgElement): View {
    return SkiaWidgetView(this, androidSkiaWidget(svg), null, null)
}

fun Context.plotView(
    plotSpec: MutableMap<String, Any>,
    plotSize: DoubleVector? = null,
    plotMaxWidth: Double? = null,
    computationMessagesHandler: ((List<String>) -> Unit)
): View {
    return buildPlotFromRawSpecs(plotSpec, plotSize, plotMaxWidth, computationMessagesHandler)
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
    }
}

