package org.jetbrains.letsPlot.android.canvas

import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.ContextStateDelegate

class AndroidContext2d(
    private val stateDelegate: ContextStateDelegate = ContextStateDelegate(failIfNotImplemented = false, logEnabled = true),
) : Context2d by stateDelegate {
}