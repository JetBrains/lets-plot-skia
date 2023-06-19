package org.jetbrains.letsPlot.skia.svg.view

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec

interface SkikoViewEventDispatcher {
    fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent)
}