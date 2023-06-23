package org.jetbrains.letsPlot.skia.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.skia.compose.android.PlotComponentProvider
import org.jetbrains.letsPlot.skia.compose.android.PlotViewContainer
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger

private const val PLOT_PANEL_DISPOSABLE_EFFECT_KEY = "Dispose only when leaves the composition."
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
        computationMessagesHandler
    )

    DisposableEffect(PLOT_PANEL_DISPOSABLE_EFFECT_KEY) {
        onDispose {
            LOG.print("DisposableEffect preserveAspectRatio: ${provider.plotViewContainer?.preserveAspectRatio} ")
            provider.dispose()
        }
    }

    AndroidView(
        factory = provider.factory,
        modifier = modifier,
        update = { plotViewContainer ->
            plotViewContainer as PlotViewContainer
            LOG.print("UPDATE PlotViewContainer preserveAspectRatio ${plotViewContainer.preserveAspectRatio} ->  $preserveAspectRatio")

            plotViewContainer.invalidatePlotView()
            plotViewContainer.figure = figure
            plotViewContainer.preserveAspectRatio = preserveAspectRatio
            plotViewContainer.revalidatePlotView()
        }
    )
}

