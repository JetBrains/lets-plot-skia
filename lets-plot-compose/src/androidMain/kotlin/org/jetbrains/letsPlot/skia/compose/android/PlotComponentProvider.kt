/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

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

    fun dispose() {
        LOG.print("dispose PlotComponentProvider preserveAspectRatio: ${plotViewContainer?.preserveAspectRatio}")
        plotViewContainer?.disposePlotView()
        plotViewContainer = null
    }
}


