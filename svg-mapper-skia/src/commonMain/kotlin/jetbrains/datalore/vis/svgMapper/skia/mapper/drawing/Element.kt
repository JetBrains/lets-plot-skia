/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia.mapper.drawing

import org.jetbrains.skia.*
import kotlin.properties.Delegates.observable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal typealias SkPath = org.jetbrains.skia.Path

internal const val INIFITY_BOUNDS = false

abstract class Element: Drawable() {
    var parent: Parent? by visualProp(null)
    var styleClass: List<String>? by visualProp(null)
    var transform: Matrix33? by visualProp(null)
    var clipPath: SkPath? by visualProp(null)
    var isVisible: Boolean by visualProp(true)

    private val propertyDeps = mutableMapOf<KProperty<*>, MutableList<DependencyProperty<*>>>()

    final override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return
        if (!isVisible) return

        canvas.save()
        transform?.let(canvas::concat)
        clipPath?.let(canvas::clipPath)
        doDraw(canvas)
        canvas.restore()
    }

    final override fun onGetBounds(): Rect {
        return if (INIFITY_BOUNDS) {
            Rect.Companion.makeWH(1000f, 1000f)
        } else {
            doGetBounds()
        }
    }

    protected open fun doDraw(canvas: Canvas) {}

    // FIXME: position mostly local to parent, but should be absolute.
    // Used by SvgSkiaPeer.getBBox via `bounds` property.
    // Also used by skia for rendering optimization. Now works only because of Pane, reporting whole canvas size.
    protected open fun doGetBounds(): Rect { return Rect.makeWH(0.0f, 0.0f)}

    protected fun repaint() {
        notifyDrawingChanged()
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

    override fun toString(): String {
        return "class: ${this::class.simpleName}, absOffset($absoluteOffsetX, $absoluteOffsetY)"
    }
}
