/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia.mapper.drawing

import jetbrains.datalore.base.observable.collections.CollectionItemEvent
import jetbrains.datalore.base.observable.collections.list.ObservableArrayList
import jetbrains.datalore.base.observable.collections.list.ObservableList
import jetbrains.datalore.base.observable.event.EventHandler
import org.jetbrains.skia.Canvas

abstract class Parent: Element() {
    protected val translateX: Float = transform?.translate?.x ?: 0f
    protected val translateY: Float = transform?.translate?.y ?: 0f

    abstract val offsetX: Float
    abstract val offsetY: Float

    val children: ObservableList<Element> = ObservableArrayList()

    init {
        children.addHandler(object : EventHandler<CollectionItemEvent<out Element>> {
            override fun onEvent(event: CollectionItemEvent<out Element>) {
                event.newItem?.parent = this@Parent
                repaint()
            }
        })
    }

    override fun doDraw(canvas: Canvas) {
        children.forEach {
            it.draw(canvas)
        }
    }

//    override fun onGetBounds(): Rect {
//        val rects = children.map { it.bounds }
//        val left = rects.minOfOrNull { it.left } ?: 0.0f
//        val top = rects.minOfOrNull { it.top } ?: 0.0f
//        val right = rects.maxOfOrNull { it.right } ?: 0.0f
//        val bottom = rects.maxOfOrNull { it.bottom } ?: 0.0f
//        return Rect(left, top, right, bottom).offset(getTranslate())
//    }
}