package jetbrains.datalore.vis.svgMapper.skia

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.skia.MonolithicSwing.buildPlotFromRawSpecs
import org.jetbrains.skiko.SkiaLayer
import javax.swing.JComponent

fun svgComponent(svg: SvgSvgElement): JComponent {
    return SkiaWidgetPanel(swingSkiaWidget(svg))
}

fun plotComponent(
    plotSpec: MutableMap<String, Any>,
    plotSize: DoubleVector? = null,
    plotMaxWidth: Double? = null,
    computationMessagesHandler: (List<String>) -> Unit = {}
): JComponent {
    return buildPlotFromRawSpecs(plotSpec, plotSize, plotMaxWidth, computationMessagesHandler)
}

internal fun swingSkiaWidget(svg: SvgSvgElement): SkiaWidget {
    return SkiaWidget(svg, SkiaLayer()) { skiaLayer, skikoView ->
        // https://github.com/JetBrains/skiko/issues/614
        //skiaLayer.skikoView = skikoView
        skiaLayer.addView(skikoView)
    }
}
