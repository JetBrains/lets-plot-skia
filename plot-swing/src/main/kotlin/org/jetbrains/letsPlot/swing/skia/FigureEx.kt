package org.jetbrains.letsPlot.swing.skia

import jetbrains.datalore.base.geometry.DoubleVector
import org.jetbrains.letsPlot.skia.awt.MonolithicSkiaAwt
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.intern.toSpec
import javax.swing.JComponent

fun Figure.createSkikoSwingComponent(
    plotSize: DoubleVector? = null,
    plotMaxWidth: Double? = null,
    computationMessagesHandler: (List<String>) -> Unit
): JComponent {
    val rawPlotSpec = this.toSpec()
    return MonolithicSkiaAwt.buildPlotFromRawSpecs(rawPlotSpec, plotSize, plotMaxWidth, computationMessagesHandler)
}