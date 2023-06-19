/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.skia.*
import kotlin.properties.Delegates.observable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal typealias SkPath = org.jetbrains.skia.Path

internal abstract class Element {
    val drawable: Drawable = object : Drawable() {
        override fun onDraw(canvas: Canvas?) {
            if (canvas == null) return
            if (!isVisible) return

            canvas.save()
            // TODO: use screenTransform?
            transform?.let(canvas::concat)
            clipPath?.let(canvas::clipPath)
            doDraw(canvas)
            canvas.restore()
        }

        override fun onGetBounds(): Rect = screenBounds
    }

    var parent: Parent? by visualProp(null)
    var styleClass: List<String>? by visualProp(null)
    var transform: Matrix33? by visualProp(null)
    var clipPath: SkPath? by visualProp(null)
    var isVisible: Boolean by visualProp(true)

    // TODO: perf. Update only if changed
    open val localBounds: Rect = Rect.Companion.makeWH(0f, 0f)

    // TODO: perf. Update only if changed
    open val screenBounds: Rect
        get() = screenTransform.apply(localBounds)

    // TODO: perf. Cache. Update only on a transform change in a tree.
    val screenTransform: Matrix33
        get() =
            parents
                .mapNotNull(Parent::transform)
                .fold(Matrix33.IDENTITY, Matrix33::makeConcat)
                .makeConcat(transform ?: Matrix33.IDENTITY)

    private val propertyDeps = mutableMapOf<KProperty<*>, MutableList<DependencyProperty<*>>>()

    protected open fun doDraw(canvas: Canvas) {}

    // TODO: perf. Update only on a tree change
    private val parents: List<Parent>
        get() {
            var p = parent
            val parents = mutableListOf<Parent>()
            while (p != null) {
                parents.add(0, p)
                p = p.parent
            }
            return parents
        }

    protected fun repaint() {
        drawable.notifyDrawingChanged()
    }

    protected fun <T> visualProp(initialValue: T): ReadWriteProperty<Any?, T> =
        observable(initialValue) { property, oldValue, newValue ->
            if (oldValue != newValue) {
                this.propertyDeps.getOrElse(property, ::emptyList).forEach(DependencyProperty<*>::invalidate)
                this.repaint()
            }
        }

    protected fun <T> dependencyProp(vararg deps: KProperty<*>, delegate: () -> T): DependencyProperty<T> =
        DependencyProperty(delegate).also { prop ->
            deps.forEach {
                propertyDeps.getOrPut(it, ::mutableListOf).add(prop)
            }
        }

    protected open fun repr(): String? = null

    override fun toString(): String {
        val repr = repr()?.let { ", $it" }
        return "class: ${this::class.simpleName}${repr ?: ""}${
            transform?.mat?.let {
                ", transform: ${
                    it.joinToString(
                        transform = Float::toString
                    )
                }"
            } ?: ""
        }"
    }
}
