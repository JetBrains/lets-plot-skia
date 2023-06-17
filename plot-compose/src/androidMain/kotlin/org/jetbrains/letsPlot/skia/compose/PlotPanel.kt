package org.jetbrains.letsPlot.skia.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.skia.compose.android.PlotComponentProvider


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
        computationMessagesHandler
    )

    DisposableEffect(provider) {
        onDispose {
            provider.onDispose()
        }
    }

    AndroidView(
        factory = provider.factory,
        modifier = modifier,
        update = NoOpUpdate
    )
}

