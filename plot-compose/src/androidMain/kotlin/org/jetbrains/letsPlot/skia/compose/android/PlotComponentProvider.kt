package org.jetbrains.letsPlot.skia.compose.android

import android.content.Context
import android.view.View
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger

private val LOG = NaiveLogger("PlotComponentProvider")

internal class PlotComponentProvider(
    private val computationMessagesHandler: ((List<String>) -> Unit)
) {
    var plotViewContainer: PlotViewContainer? = null

    val factory: (Context) -> View = { ctx ->
        check(plotViewContainer == null) { "An attempt to reuse a single-use view factory." }
        PlotViewContainer(
            ctx,
            computationMessagesHandler
        ).also {
            plotViewContainer = it
        }
    }

    init {
        LOG.print("New PlotComponentProvider")
    }

    fun onDispose() {
        LOG.print("dispose PlotComponentProvider preserveAspectRatio: ${plotViewContainer?.preserveAspectRatio}")
        plotViewContainer?.disposePlotView()
        plotViewContainer = null
    }
}


