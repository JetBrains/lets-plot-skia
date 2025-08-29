/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.android.canvas.CanvasView
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import org.jetbrains.letsPlot.raster.view.PlotCanvasFigure
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger

private val LOG = NaiveLogger("PlotPanel")

@Suppress("FunctionName")
@Composable
actual fun PlotPanel(
    figure: Figure,
    preserveAspectRatio: Boolean,
    modifier: Modifier,
    computationMessagesHandler: (List<String>) -> Unit
) {
    var plotCanvasFigure by remember { mutableStateOf(PlotCanvasFigure()) }

    LOG.print { "Recompose PlotPanel()" }

    AndroidView(
        factory = { ctx ->
            LOG.print { "PlotPanel: AndroidView factory called" }
            val canvasView = CanvasView(ctx)
            canvasView.figure = plotCanvasFigure
            canvasView
        },
        modifier = modifier,
        update = { canvasView ->
            MonolithicCanvas.updatePlotFigureFromRawSpec(
                plotCanvasFigure = plotCanvasFigure,
                rawSpec = figure.toSpec(),
                sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio),
                computationMessagesHandler = computationMessagesHandler
            )
        }
    )
}
