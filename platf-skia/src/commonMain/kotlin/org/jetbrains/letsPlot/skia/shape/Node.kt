/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.skia.mapping.svg.DebugOptions.VALIDATE_MANAGED_PROPERTIES
import org.jetbrains.skia.impl.Managed
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal abstract class Node {
    var id: String? = null
    var isVisible: Boolean by visualProp(true)

    // visual prop -> list of dependent dependency properties updaters
    private val dependentComputedProperties = mutableMapOf<KProperty<*>, List<ComputedProperty<*>>>()
    private val computedProperties = mutableMapOf<KProperty<*>, ComputedProperty<*>>()
    private val managedProperties = mutableMapOf<KProperty<*>, () -> Managed?>()

    internal fun invalidateComputedProp(prop: KProperty<*>) {
        computedProperties[prop]?.invalidate()
            ?: error { "Class `${this::class.simpleName}` doesn't have computedProperty `${prop.name}`" }
    }

    private fun handlePropertyChange(property: KProperty<*>, oldValue: Any?, newValue: Any?) {
        if (property in managedProperties && oldValue is Managed) {
            oldValue.close()
        }

        onPropertyChanged(property)

        dependentComputedProperties[property]?.forEach(ComputedProperty<*>::invalidate)
    }

    fun <T> computedProp(
        vararg deps: KProperty<*>,
        valueProvider: () -> T
    ): PropertyDelegateProvider<Node, ReadOnlyProperty<Node, T>> {
        return computedProp(dependencies = deps, managed = false, valueProvider)
    }

    fun <T> computedProp(
        vararg dependencies: KProperty<*>,
        managed: Boolean,
        valueProvider: () -> T,
    ): PropertyDelegateProvider<Node, ReadOnlyProperty<Node, T>> {
        return PropertyDelegateProvider<Node, ReadOnlyProperty<Node, T>> { thisRef, property ->
            val computedProperty = ComputedProperty(valueProvider, thisRef::handlePropertyChange)

            if (managed) {
                thisRef.managedProperties[property] = { computedProperty.getValue(thisRef, property) as Managed? }
            }

            dependencies.forEach {
                dependentComputedProperties[it] = (dependentComputedProperties[it] ?: emptyList()) + computedProperty
            }

            computedProperties[property] = computedProperty

            return@PropertyDelegateProvider computedProperty
        }
    }

    fun <T> visualProp(
        initialValue: T,
        managed: Boolean = false
    ): PropertyDelegateProvider<Node, ReadWriteProperty<Node, T>> {
        return PropertyDelegateProvider<Node, ReadWriteProperty<Node, T>> { thisRef, property ->
            val visualProperty = VisualProperty(initialValue, thisRef::handlePropertyChange)

            if (managed) {
                thisRef.managedProperties[property] = { visualProperty.getValue(thisRef, property) as Managed? }
            }

            return@PropertyDelegateProvider visualProperty
        }
    }

    fun release() {
        managedProperties
            .values
            .mapNotNull { it() }
            .filterNot(Managed::isClosed)
            .forEach(Managed::close)
    }

    protected open fun onPropertyChanged(prop: KProperty<*>) {}
    protected open fun repr(): String? = null

    override fun toString(): String {
        val idStr = id?.let { "id: '$it' " } ?: ""
        return "class: ${this::class.simpleName}$idStr${repr()}"
    }
}
