/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableArrayList
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableList
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.skia.Rect
import kotlin.reflect.KProperty

internal abstract class Container : Element() {
    val children: ObservableList<Element> = ObservableArrayList()

    init {
        children.addHandler(object : EventHandler<CollectionItemEvent<out Element>> {
            override fun onEvent(event: CollectionItemEvent<out Element>) {
                when (event.type) {
                    CollectionItemEvent.EventType.ADD -> event.newItem?.let { it.parent = this@Container }
                    CollectionItemEvent.EventType.REMOVE -> event.oldItem?.let { it.parent = null }
                    CollectionItemEvent.EventType.SET -> {
                        event.oldItem?.let { it.parent = null }
                        event.newItem?.let { it.parent = this@Container }
                    }
                }
            }
        })
    }

    override fun onPropertyChanged(prop: KProperty<*>) {
        if (prop == Element::transform) {
            breadthFirstTraversal(this).forEach { it.invalidateComputedProp(Element::ctm) }
        }

        if (prop == Element::ctm) {
            breadthFirstTraversal(this).forEach { it.invalidateComputedProp(Element::ctm) }
        }

        if (prop == Element::parent) {
            invalidateHierarchy(this)
        }
    }

    override val localBounds: Rect
        get() = children
            .filterNot { it is Container && it.children.isEmpty() }
            .map(Element::localBounds)
            .let(::union)
            ?: Rect.makeWH(0.0f, 0.0f)

    override val screenBounds: Rect
        get() {
            return children
                .filterNot { it is Container && it.children.isEmpty() }
                .map(Element::screenBounds)
                .let(::union)
                ?: Rect.makeXYWH(ctm.translateX, ctm.translateY, 0.0f, 0.0f)
        }


    private fun invalidateHierarchy(e: Element) {
        e.invalidateComputedProp(Element::parents)
        e.invalidateComputedProp(Element::ctm)
        breadthFirstTraversal(e).forEach {
            it.invalidateComputedProp(Element::parents)
            it.invalidateComputedProp(Element::ctm)
        }
    }
}
