package org.jetbrains.letsPlot.skia.svg.view

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec

interface SkikoViewEventDispatcher {
    fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent)
}