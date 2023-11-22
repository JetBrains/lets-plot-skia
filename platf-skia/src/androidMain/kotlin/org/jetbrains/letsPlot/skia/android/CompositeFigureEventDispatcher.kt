/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.android

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.skia.view.SkikoViewEventDispatcher

internal class CompositeFigureEventDispatcher() : SkikoViewEventDispatcher {
    private val dispatchers = LinkedHashMap<Rectangle, SkikoViewEventDispatcher>()

    fun addEventDispatcher(bounds: DoubleRectangle, eventDispatcher: SkikoViewEventDispatcher) {
        val rect = Rectangle(
            bounds.origin.x.toInt(),
            bounds.origin.y.toInt(),
            bounds.dimension.x.toInt(),
            bounds.dimension.y.toInt()
        )
        dispatchers[rect] = eventDispatcher
    }

    override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
        val loc = Vector(e.x, e.y)
        val target = dispatchers.keys.find { it.contains(loc) }
        if (target != null) {
            val dispatcher = dispatchers.getValue(target)
            dispatcher.dispatchMouseEvent(
                kind,
                MouseEvent(
                    v = Vector(loc.x - target.origin.x, loc.y - target.origin.y),
                    button = e.button,
                    modifiers = e.modifiers
                )
            )
        }
    }
}