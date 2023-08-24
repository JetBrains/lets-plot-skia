/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableArrayList
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableList
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect
import kotlin.math.max
import kotlin.math.min

internal abstract class Parent : Element() {
    val children: ObservableList<Element> = ObservableArrayList()

    init {
        children.addHandler(object : EventHandler<CollectionItemEvent<out Element>> {
            override fun onEvent(event: CollectionItemEvent<out Element>) {
                when (event.type) {
                    CollectionItemEvent.EventType.ADD -> event.newItem?.parent = this@Parent
                    CollectionItemEvent.EventType.REMOVE -> event.oldItem?.parent = null
                    CollectionItemEvent.EventType.SET -> {
                        event.oldItem?.parent = null
                        event.newItem?.parent = this@Parent
                    }
                }

                needRedraw()
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

    override val localBounds: Rect
        get() = children
            .filterNot { it is Parent && it.children.isEmpty() }
            .fold<Element, Rect?>(null) { acc, element ->
                if (acc != null) {
                    Rect.makeLTRB(
                        min(acc.left, element.localBounds.left),
                        min(acc.top, element.localBounds.top),
                        max(acc.right, element.localBounds.right),
                        max(acc.bottom, element.localBounds.bottom)
                    )
                } else {
                    element.localBounds
                }
            } ?: Rect.makeWH(0.0f, 0.0f)

    override val screenBounds: Rect
        get() {
            return children
                .filterNot { it is Parent && it.children.isEmpty() }
                .fold<Element, Rect?>(null) { acc, element ->
                    if (acc != null) {
                        element.screenBounds.let {
                            Rect.makeLTRB(
                                min(acc.left, it.left),
                                min(acc.top, it.top),
                                max(acc.right, it.right),
                                max(acc.bottom, it.bottom)
                            )
                        }
                    } else {
                        element.screenBounds
                    }
                } ?: Rect.makeXYWH(ctm.translateX, ctm.translateY, 0.0f, 0.0f)
        }


}