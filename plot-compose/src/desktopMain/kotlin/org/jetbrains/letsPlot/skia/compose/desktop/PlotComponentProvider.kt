package org.jetbrains.letsPlot.skia.compose.desktop

import jetbrains.datalore.base.geometry.DoubleVector
import java.awt.Component
import javax.swing.Timer

internal class PlotComponentProvider(
    repaintDelay: Int, // ms
    computationMessagesHandler: ((List<String>) -> Unit)
) {

    var plotViewContainer: PlotViewContainer? = null
    private var containerSize: DoubleVector? = null

    private val refreshTimer: Timer = Timer(repaintDelay) {
        if (containerSize != null && plotViewContainer != null) {
            plotViewContainer!!.revalidatePlotView(containerSize!!)
        }
    }.apply { isRepeats = false }

    val factory: () -> Component = {
        check(plotViewContainer == null) { "An attempt to reuse a single-use view factory." }
        PlotViewContainer(
            computationMessagesHandler
        ).also {
            plotViewContainer = it
        }
    }

    fun onGloballyPositioned(w: Float, h: Float) {
        plotViewContainer?.invalidatePlotView()

        containerSize = DoubleVector(w.toDouble(), h.toDouble())
        if (refreshTimer.isRunning) {
            refreshTimer.restart()
        } else {
            refreshTimer.start()
        }
    }

    fun dispose() {
        containerSize = null
        if (refreshTimer.isRunning) {
            refreshTimer.stop()
        }
        plotViewContainer?.disposePlotView()
    }
}