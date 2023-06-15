package org.jetbrains.letsPlot.skia.compose

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skia.android.MonolithicSkiaAndroid.buildPlotFromRawSpecs


@Suppress("FunctionName")
@Composable
actual fun PlotPanel(
    figure: Figure,
    modifier: Modifier,
    computationMessagesHandler: ((List<String>) -> Unit)
) {
    val rawPlotSpec = figure.toSpec()
    val factory: (Context) -> View = { ctx ->
        buildPlotFromRawSpecs(
            ctx,
            rawPlotSpec,
            plotSize = null,
            computationMessagesHandler = computationMessagesHandler
        )
    }

    AndroidView(
        factory = factory,
        modifier = modifier,
        update = NoOpUpdate
    )
}

