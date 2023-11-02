/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.skia.compose.desktop.PlotComponentProvider
import org.jetbrains.letsPlot.skia.compose.desktop.PlotViewContainer
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
    LOG.print("Recompose PlotPanel() preserveAspectRatio: $preserveAspectRatio ")

    var size by remember { mutableStateOf(DoubleVector(0.0, 0.0)) }

    val provider by remember {
        mutableStateOf(
            PlotComponentProvider(
                repaintDelay = 0, // Should be zero to work in Compose 1.4.1
                computationMessagesHandler
            )
        )
    }

    DisposableEffect(provider) {
        onDispose {
            LOG.print("DisposableEffect preserveAspectRatio: ${provider.plotViewContainer?.preserveAspectRatio} ")
            //provider.dispose()
            provider.plotViewContainer?.disposePlotView()
        }
    }

    SwingPanel(
        background = Color.White,
        factory = provider.factory,
        modifier = modifier.onSizeChanged {
            LOG.print("modifier.onSizeChanged: $it")
            val newSize = DoubleVector(it.width.toDouble(), it.height.toDouble())
            if (newSize != size) {
                size = newSize

                // TODO: mode resize logic to PlotViewContainer with ComponentAdapter
                // TODO: investigate, why size change should be handled within onSizeChanged.
                // Not calling `provider.resize()` here and calling `revalidatePlotView(size)` in `update()` instead
                // leads to empty plot view until next recomposition.
                // Same happens with `ComponentAdapter` in PlotViewContainer, coroutine delay, debounce.

                provider.plotViewContainer?.rebuildPlotView(newSize)
            }
        },
        update = { plotViewContainer ->
            plotViewContainer as PlotViewContainer
            LOG.print("UPDATE PlotViewContainer preserveAspectRatio ${plotViewContainer.preserveAspectRatio} ->  $preserveAspectRatio, size: $size")

            if (
                plotViewContainer.figure != figure
                || plotViewContainer.preserveAspectRatio != preserveAspectRatio
            ) {
                plotViewContainer.figure = figure
                plotViewContainer.preserveAspectRatio = preserveAspectRatio

                plotViewContainer.rebuildPlotView(size)
            }
        }
    )
}