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

    private val debouncedRefresh: DebouncedRunner = debounce(repaintDelay) {
        if (containerSize != null && plotViewContainer != null) {
            plotViewContainer!!.revalidatePlotView(containerSize!!)
        }
    }

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
        debouncedRefresh.run()
    }

    fun dispose() {
        containerSize = null
        debouncedRefresh.cancel()
        plotViewContainer?.disposePlotView()
    }
}