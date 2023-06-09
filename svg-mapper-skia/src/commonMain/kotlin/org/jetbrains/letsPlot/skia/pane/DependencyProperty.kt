package org.jetbrains.letsPlot.skia.pane

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class DependencyProperty<T>(
    private val delegate: () -> T
) : ReadOnlyProperty<Any?, T> {
    private var isDirty: Boolean = false
    private var value: T = delegate()

    fun invalidate() {
        isDirty = true
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (isDirty) {
            isDirty = false
            value = delegate()
        }
        return value
    }
}