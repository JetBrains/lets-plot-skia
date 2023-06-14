package org.jetbrains.letsPlot.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.NoOpUpdate
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skia.awt.MonolithicSkiaAwt
import java.awt.Component

@Suppress("FunctionName")
@Composable
actual fun PlotPanel(
    figure: Figure,
    modifier: Modifier,
    computationMessagesHandler: ((List<String>) -> Unit)
) {
    val rawPlotSpec = figure.toSpec()
    val factory: () -> Component = {
        MonolithicSkiaAwt.buildPlotFromRawSpecs(
            rawPlotSpec,
            plotSize = null,
            computationMessagesHandler = computationMessagesHandler
        )
    }

    SwingPanel(
        background = Color.White,
        factory = factory,
        modifier = modifier,
        update = NoOpUpdate
    )
}