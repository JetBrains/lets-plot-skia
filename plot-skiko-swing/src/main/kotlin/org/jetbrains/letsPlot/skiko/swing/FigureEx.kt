package org.jetbrains.letsPlot.skiko.swing

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.svgMapper.skia.MonolithicSkikoSwing
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.intern.toSpec
import javax.swing.JComponent

fun Figure.createSkikoSwingComponent(
    plotSize: DoubleVector? = null,
    plotMaxWidth: Double? = null,
    computationMessagesHandler: (List<String>) -> Unit
): JComponent {
    val rawPlotSpec = this.toSpec()
    return MonolithicSkikoSwing.buildPlotFromRawSpecs(rawPlotSpec, plotSize, plotMaxWidth, computationMessagesHandler)
}