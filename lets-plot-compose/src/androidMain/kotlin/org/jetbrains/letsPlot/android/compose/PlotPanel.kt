/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.android.compose

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger

private val LOG = NaiveLogger("PlotPanel")

// TODO: Investigate memory leaks and repaints without data changes.
@Suppress("FunctionName")
@Composable
fun PlotPanel(
    figure: Figure,
    preserveAspectRatio: Boolean,
    modifier: Modifier,
    computationMessagesHandler: (List<String>) -> Unit
) {
    LOG.print { "Recompose PlotPanel() preserveAspectRatio: $preserveAspectRatio " }

    val provider by remember {
        mutableStateOf(
            CanvasViewProvider(
                computationMessagesHandler
            )
        )
    }

    DisposableEffect(provider) {
        onDispose {
            LOG.print { "DisposableEffect" }
            provider.dispose()
        }
    }

    AndroidView(
        factory = provider.factory,
        modifier = modifier,
        update = { canvasView ->
            val plotFigure = MonolithicCanvas.buildPlotFigureFromRawSpec(
                figure.toSpec(),
                sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio)
            ) { println("Computation messages: $it") }
            LOG.print { "UPDATE PlotViewContainer preserveAspectRatio ${canvasView.figure} ->  $preserveAspectRatio" }

            canvasView.figure = plotFigure
        }
    )
}

