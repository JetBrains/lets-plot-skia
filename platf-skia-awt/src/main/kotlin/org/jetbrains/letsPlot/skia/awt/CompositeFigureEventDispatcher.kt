/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.awt

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.skia.view.SkikoViewEventDispatcher
import java.awt.Point
import java.awt.Rectangle

internal class CompositeFigureEventDispatcher() : SkikoViewEventDispatcher {
    private val dispatchers = LinkedHashMap<Rectangle, SkikoViewEventDispatcher>()

    fun addEventDispatcher(bounds: Rectangle, eventDispatcher: SkikoViewEventDispatcher) {
        dispatchers[bounds] = eventDispatcher
    }

    override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
        val loc = Point(e.x, e.y)
        val target = dispatchers.keys.find { it.contains(loc) }
        if (target != null) {
            val dispatcher = dispatchers.getValue(target)
            dispatcher.dispatchMouseEvent(
                kind,
                MouseEvent(
                    v = Vector(loc.x - target.x, loc.y - target.y),
                    button = e.button,
                    modifiers = e.modifiers
                )
            )
        }
    }
}