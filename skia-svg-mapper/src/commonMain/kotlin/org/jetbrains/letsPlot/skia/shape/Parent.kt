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
import kotlin.reflect.KProperty

internal abstract class Parent : Element() {
    val children: ObservableList<Element> = ObservableArrayList()

    init {
        children.addHandler(object : EventHandler<CollectionItemEvent<out Element>> {
            override fun onEvent(event: CollectionItemEvent<out Element>) {
                when (event.type) {
                    CollectionItemEvent.EventType.ADD -> {
                        event.newItem?.let {
                            it.parent = this@Parent
                            invalidateHierarchy(it)
                        }
                    }

                    CollectionItemEvent.EventType.REMOVE -> {
                        event.oldItem?.let {
                            it.parent = null
                            invalidateHierarchy(it)
                        }
                    }

                    CollectionItemEvent.EventType.SET -> {
                        event.oldItem?.let {
                            it.parent = null
                            invalidateHierarchy(it)
                        }
                        event.newItem?.let {
                            it.parent = this@Parent
                            invalidateHierarchy(it)
                        }
                    }
                }

                needRedraw()
            }
        })
    }

    override fun onPropertyChanged(prop: KProperty<*>) {
        if (prop == Element::transform) {
            flattenChildren(this).forEach { it.invalidateDependencyProp(Element::ctm) }
        }

        if (prop == Element::ctm) {
            flattenChildren(this).forEach { it.invalidateDependencyProp(Element::ctm) }
        }
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
            .map(Element::localBounds)
            .let(::union)
            ?: Rect.makeWH(0.0f, 0.0f)

    override val screenBounds: Rect
        get() {
            return children
                .filterNot { it is Parent && it.children.isEmpty() }
                .map(Element::screenBounds)
                .let(::union)
                ?: Rect.makeXYWH(ctm.translateX, ctm.translateY, 0.0f, 0.0f)
        }


    private fun invalidateHierarchy(e: Element) {
        e.invalidateDependencyProp(Element::parents)
        e.invalidateDependencyProp(Element::ctm)
        flattenChildren(e).forEach {
            it.invalidateDependencyProp(Element::parents)
            it.invalidateDependencyProp(Element::ctm)
        }
    }
}
