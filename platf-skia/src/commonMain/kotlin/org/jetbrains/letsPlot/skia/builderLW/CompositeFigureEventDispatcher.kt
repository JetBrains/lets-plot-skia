/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.builderLW

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.skia.view.SkikoViewEventDispatcher

class CompositeFigureEventDispatcher() : SkikoViewEventDispatcher {
    private val dispatchers = LinkedHashMap<Rectangle, SkikoViewEventDispatcher>()

    fun addEventDispatcher(bounds: Rectangle, eventDispatcher: SkikoViewEventDispatcher) {
        dispatchers[bounds] = eventDispatcher
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

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        // Looks like composite figures in Compose don't need this method because they are all placed in the same
        // container and this container is a dispatcher for all of them.
        return Registration.EMPTY
    }
}