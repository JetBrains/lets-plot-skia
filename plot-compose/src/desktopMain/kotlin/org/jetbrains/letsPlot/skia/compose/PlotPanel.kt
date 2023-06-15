package org.jetbrains.letsPlot.skia.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.NoOpUpdate
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.skia.compose.desktop.PlotComponentProvider

@Suppress("FunctionName")
@Composable
actual fun PlotPanel(
    figure: Figure,
    modifier: Modifier,
    computationMessagesHandler: ((List<String>) -> Unit)
) {

    val provider = PlotComponentProvider(
        figure = figure,
        preserveAspectRatio = false,
        repaintDelay = 300,
        computationMessagesHandler
    )

    DisposableEffect(provider) {
        onDispose {
            provider.onDispose()
        }
    }

    val density = LocalDensity.current.density

    @Suppress("NAME_SHADOWING")
    val modifier = modifier.onGloballyPositioned { coordinates ->
        val size = coordinates.size
        val width = size.width / density
        val height = size.height / density
        provider.onGloballyPositioned(width, height)
    }

    SwingPanel(
        background = Color.White,
        factory = provider.factory,
        modifier = modifier,
        update = NoOpUpdate
    )
}