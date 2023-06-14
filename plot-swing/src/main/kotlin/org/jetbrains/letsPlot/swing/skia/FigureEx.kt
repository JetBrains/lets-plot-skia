package org.jetbrains.letsPlot.swing.skia

import jetbrains.datalore.plot.MonolithicCommon
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.intern.toSpec
import javax.swing.JComponent

fun Figure.createComponent(
    preserveAspectRatio: Boolean = false,
    preferredSizeFromPlot: Boolean = false,
    repaintDelay: Int = 300,  // ms,
    computationMessagesHandler: (List<String>) -> Unit
): JComponent {
    val rawSpec = this.toSpec()
    val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)

    return PlotPanelSkia(
        processedSpec = processedSpec,
        preserveAspectRatio = preserveAspectRatio,
        preferredSizeFromPlot = preferredSizeFromPlot,
        repaintDelay = repaintDelay,
        computationMessagesHandler = computationMessagesHandler
    )
}