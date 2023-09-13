/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.skia.svg.mapper.DebugOptions.VALIDATE_MANAGED_PROPERTIES
import org.jetbrains.skia.impl.Managed
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal abstract class Node {
    var id: String? = null
    var isVisible: Boolean by visualProp(true)

    private var released: Boolean = false
    private val resourcesDisposer = mutableListOf<() -> Unit>()
    // visual prop -> list of dependent dependency properties updaters
    private val dependenciesUpdater = mutableMapOf<KProperty<*>, MutableList<() -> Unit>>()
    private val computedPropertiesUpdater = mutableMapOf<KProperty<*>, () -> Unit>()

    internal fun invalidateDependencyProp(prop: KProperty<*>) {
        require(prop in computedPropertiesUpdater) {
            "Class `${this::class.simpleName}` doesn't have dependencyProperty `${prop.name}`"
        }

        computedPropertiesUpdater[prop]!!.invoke()
    }

    protected open fun onPropertyChanged(prop: KProperty<*>) {}

    inline fun <reified T> visualProp(initialValue: T, managed: Boolean = false): PropertyDelegateProvider<Node, ReadWriteProperty<Node, T>> {
        return PropertyDelegateProvider<Node, ReadWriteProperty<Node, T>> { thisRef, property ->
            val delegate = object : ReadWriteProperty<Node, T> {
                var value = initialValue
                override fun getValue(thisRef: Node, property: KProperty<*>): T = value
                override fun setValue(thisRef: Node, property: KProperty<*>, value: T) = run {
                    val oldValue = this.value
                    this.value = value

                    if (VALIDATE_MANAGED_PROPERTIES && !managed) {
                        require(value == null || value !is Managed) { "Property ${property.name} should be marked as managed" }
                    }

                    if (oldValue != value) {
                        if (oldValue is Managed) {
                            oldValue.close()
                        }

                        onPropertyChanged(property)
                        thisRef.dependenciesUpdater.getOrElse(property, ::emptyList).forEach { it() }
                    }
                }
            }

            if (managed) {
                thisRef.resourcesDisposer.add { (delegate.getValue(thisRef, property) as? Managed)?.close() }
            }

            delegate
        }
    }

    internal inline fun <reified T> dependencyProp(vararg deps: KProperty<*>, crossinline valueProvider: () -> T): PropertyDelegateProvider<Node, ReadOnlyProperty<Node, T>> {
        return dependencyProp(deps = deps, managed = false, valueProvider)
    }

    internal inline fun <reified T> dependencyProp(vararg deps: KProperty<*>, managed: Boolean, crossinline valueProvider: () -> T): PropertyDelegateProvider<Node, ReadOnlyProperty<Node, T>> {
        return PropertyDelegateProvider<Node, ReadOnlyProperty<Node, T>> { thisRef, property ->
            val computedProperty = object : ReadOnlyProperty<Node, T> {
                private var isDirty: Boolean = false
                private var value: T = valueProvider()

                fun invalidate() {
                    isDirty = true
                }

                override fun getValue(thisRef: Node, property: KProperty<*>): T {
                    if (isDirty) {
                        isDirty = false
                        val oldValue = value
                        value = valueProvider()

                        if (VALIDATE_MANAGED_PROPERTIES && !managed) {
                            require(value == null || value !is Managed) { "Property should be marked as managed" }
                        }

                        if (oldValue != value) {
                            if (oldValue is Managed) {
                                oldValue.close()
                            }
                            onPropertyChanged(property)
                        }
                    }
                    return value
                }
            }

            if (managed) {
                thisRef.resourcesDisposer.add { computedProperty.getValue(thisRef, property) as? Managed }
            }

            deps.forEach { dependenciesUpdater.getOrPut(it, ::mutableListOf).add(computedProperty::invalidate) }
            computedPropertiesUpdater[property] = computedProperty::invalidate
            computedProperty
        }
    }

    fun release() {
        resourcesDisposer.forEach { it.invoke() }
        released = true
    }

    protected open fun repr(): String? = null

    override fun toString(): String {
        val idStr = id?.let { "id: '$it' " } ?: ""
        return "class: ${this::class.simpleName}$idStr${repr()}"
    }
}
