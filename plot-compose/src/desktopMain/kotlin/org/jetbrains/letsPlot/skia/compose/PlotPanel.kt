package org.jetbrains.letsPlot.skia.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.letsPlot.Figure
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

    val provider = PlotComponentProvider(
        repaintDelay = 300,
        computationMessagesHandler
    )

    DisposableEffect(provider) {
        onDispose {
            LOG.print("DisposableEffect preserveAspectRatio: ${provider.plotViewContainer?.preserveAspectRatio} ")
            provider.dispose()
        }
    }

    val density = LocalDensity.current.density

    @Suppress("NAME_SHADOWING")
    val modifier = modifier.onGloballyPositioned { coordinates ->
        LOG.print("onGloballyPositioned")
        val size = coordinates.size
        val width = size.width / density
        val height = size.height / density
        provider.onGloballyPositioned(width, height)
    }

    SwingPanel(
        background = Color.White,
        factory = provider.factory,
        modifier = modifier,
        update = { plotViewContainer ->
            // Using the "update" block in Compose-Desktop makes actually no big sense
            // because when any "mutable state" is changed, the entire composable is rebuilt.
            // This is in contrast with Compose-Android where only the "update" block is evaluated and
            // nothing else is re-built/disposed.

            plotViewContainer as PlotViewContainer
            LOG.print("UPDATE PlotViewContainer preserveAspectRatio ${plotViewContainer.preserveAspectRatio} ->  $preserveAspectRatio")

            plotViewContainer.invalidatePlotView()
            plotViewContainer.figure = figure
            plotViewContainer.preserveAspectRatio = preserveAspectRatio

            // Leave it here and wait for 'onGloballyPositioned' event to actually revalidate the plot view.
        }
    )
}