/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose.desktop

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.skia.compose.desktop.DebouncedRunner.Companion.debounce
import java.awt.Component

internal class PlotComponentProvider(
    repaintDelay: Int, // ms
    computationMessagesHandler: ((List<String>) -> Unit)
) {
    var plotViewContainer: PlotViewContainer? = null
    private var containerSize: DoubleVector? = null

    val factory: () -> Component = {
        check(plotViewContainer == null) { "An attempt to reuse a single-use view factory." }
        PlotViewContainer(
            computationMessagesHandler
        ).also {
            plotViewContainer = it
        }
    }

    // TODO: need to fix empty plot view on debounce first
    private fun resize(size: DoubleVector) {
        containerSize = size
        debouncedRefresh.run()
    }

    private fun dispose() {
        containerSize = null
        debouncedRefresh.cancel()
        plotViewContainer?.disposePlotView()
    }

    // Better to use coroutines and delay() in composable.
    private val debouncedRefresh: DebouncedRunner = debounce(repaintDelay) {
        val containerSize = containerSize ?: return@debounce
        val plotViewContainer = plotViewContainer ?: return@debounce

        plotViewContainer.rebuildPlotView(containerSize)
    }
}