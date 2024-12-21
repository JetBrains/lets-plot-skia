/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleToolView
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
    // Update density on each recomposition to handle monitor DPI changes (e.g. drag between HIDPI/regular monitor)
    val density = LocalDensity.current.density.toDouble()

    val provider by remember {
        mutableStateOf(
            PlotComponentProvider(
                repaintDelay = 0, // Should be zero to work in Compose 1.4.1
                computationMessagesHandler
            )
        )
    }

    var panToolEnabled by remember { mutableStateOf(false) }
    var bboxZoomToolEnabled by remember { mutableStateOf(false) }
    var cboxZoomToolEnabled by remember { mutableStateOf(false) }

    val panToolView = ToggleComposeButton()
    val bboxZoomToolView = ToggleComposeButton()
    val cboxZoomToolView = ToggleComposeButton()

    DisposableEffect(provider) {
        onDispose {
            LOG.print("DisposableEffect preserveAspectRatio: ${provider.plotViewContainer?.preserveAspectRatio} ")
            //provider.dispose()
            provider.plotViewContainer?.disposePlotView()
        }
    }

    if (true){// || figure.toSpec().containsKey(Option.Meta.Kind.GG_TOOLBAR)) {
        Row {
            Button(onClick = { panToolEnabled = !panToolEnabled }) {
                Text("PAN ${if (panToolEnabled) "on" else "off"}")
            }
            Button(onClick = { bboxZoomToolEnabled = !bboxZoomToolEnabled }) {
                Text("BBOX ZOOM ${if (bboxZoomToolEnabled) "on" else "off"}")
            }
            Button(onClick = { cboxZoomToolEnabled = !cboxZoomToolEnabled }) {
                Text("CBOX ZOOM ${if (cboxZoomToolEnabled) "on" else "off"}")
            }
        }
    }
    SwingPanel(
        background = Color.White,
        factory = provider.factory,
        modifier = modifier.onSizeChanged {
            // TODO: move resize logic to PlotViewContainer with ComponentAdapter
            // TODO: investigate, why size change should be handled within onSizeChanged.
            // Not calling `rebuildPlotView()` here and calling `rebuildPlotView(size)` in `update()` instead
            // leads to an empty plot view until next recomposition.
            // Same happens with `ComponentAdapter` in PlotViewContainer, coroutine delay, debounce.

            LOG.print("modifier.onSizeChanged() - $it")
            provider.plotViewContainer?.size = DoubleVector(it.width / density, it.height / density)
            provider.plotViewContainer?.updatePlotView()
        },
        update = { plotViewContainer ->
            plotViewContainer as PlotViewContainer
            LOG.print("UPDATE PlotViewContainer preserveAspectRatio ${plotViewContainer.preserveAspectRatio} ->  $preserveAspectRatio")

            plotViewContainer.figure = figure
            plotViewContainer.preserveAspectRatio = preserveAspectRatio
            plotViewContainer.panToolEnabled = panToolEnabled
            plotViewContainer.bboxZoomToolEnabled = bboxZoomToolEnabled
            plotViewContainer.cboxZoomToolEnabled = cboxZoomToolEnabled
            plotViewContainer.updatePlotView()
        }
    )

}

private class ToggleComposeButton: ToggleToolView {
    private var selected: Boolean by mutableStateOf(false)
    private var actionHandler: (() -> Unit)? = null

    override fun setState(selected: Boolean) {
        this.selected = selected
    }

    override fun onAction(handler: () -> Unit) {
        actionHandler = handler
    }

    fun onClick() {
        actionHandler?.invoke()
    }
}