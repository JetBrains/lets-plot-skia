package jetbrains.datalore.vis.svgMapper.skia

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.svgMapper.skia.MonolithicSkikoSwing.buildPlotFromRawSpecs
import javax.swing.JComponent

fun plotComponent(
    plotSpec: MutableMap<String, Any>,
    plotSize: DoubleVector? = null,
    plotMaxWidth: Double? = null,
    computationMessagesHandler: (List<String>) -> Unit = {}
): JComponent {
    return buildPlotFromRawSpecs(plotSpec, plotSize, plotMaxWidth, computationMessagesHandler)
}
