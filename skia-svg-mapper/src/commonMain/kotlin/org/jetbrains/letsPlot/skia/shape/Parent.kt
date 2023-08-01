/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableArrayList
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableList
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.skia.Canvas

internal abstract class Parent : Element() {
    val children: ObservableList<Element> = ObservableArrayList()

    init {
        children.addHandler(object : EventHandler<CollectionItemEvent<out Element>> {
            override fun onEvent(event: CollectionItemEvent<out Element>) {
                event.newItem?.parent = this@Parent
                repaint()
            }
        })
    }

    // TODO: split visual tree and logical tree.
    // Logical parent should not affect dirty regions
    // Only visible children should report dirty regions
    override fun doDraw(canvas: Canvas) {
        children.forEach {
            it.drawable.onDraw(canvas)
        }
    }
}