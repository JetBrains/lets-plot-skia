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

private const val PLOT_PANEL_DISPOSABLE_EFFECT_KEY = "Dispose only when leaves the composition."

@Suppress("FunctionName")
@Composable
actual fun PlotPanel(
    figure: Figure,
    preserveAspectRatio: Boolean,
    modifier: Modifier,
    computationMessagesHandler: (List<String>) -> Unit
) {

    val provider = PlotComponentProvider(
        figure = figure,
        preserveAspectRatio = preserveAspectRatio,
        repaintDelay = 300,
        computationMessagesHandler
    )

//    DisposableEffect(provider) {
    DisposableEffect(PLOT_PANEL_DISPOSABLE_EFFECT_KEY) {
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
        update = NoOpUpdate  // ToDo: Update when recomposed? See Android actual.
    )
}