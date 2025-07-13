/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.android.compose

import android.content.Context
import org.jetbrains.letsPlot.android.canvas.CanvasView
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger

private val LOG = NaiveLogger("PlotComponentProvider")

internal class CanvasViewProvider(
    private val computationMessagesHandler: ((List<String>) -> Unit)
) {
    private var canvasView: CanvasView? = null

    val factory: (Context) -> CanvasView = { ctx ->
        check(canvasView == null) { "An attempt to reuse a single-use view factory." }
        CanvasView(ctx).also {
            canvasView = it
        }
    }

    fun dispose() {
        LOG.print("dispose PlotComponentProvider preserveAspectRatio: ${canvasView?.figure}")
        //plotViewContainer?.disposePlotView()
        canvasView = null
    }
}


