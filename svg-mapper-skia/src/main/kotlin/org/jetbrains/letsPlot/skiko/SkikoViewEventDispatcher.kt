package org.jetbrains.letsPlot.skiko

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec

interface SkikoViewEventDispatcher {
    fun dispatchMouseEvent(kind: MouseEventSpec, event: MouseEvent)
}