/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class VisualProperty<T>(
    initialValue: T,
    private val onPropertyChanged: (KProperty<*>, T, T) -> Unit
) : ReadWriteProperty<Any, T> {
    var value = initialValue
    override fun getValue(thisRef: Any, property: KProperty<*>): T = value
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = run {
        val oldValue = this.value
        this.value = value

        if (oldValue != value) {
            onPropertyChanged(property, oldValue, value)
        }
    }
}