package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.skia.svg.mapper.DebugOptions.VALIDATE_MANAGED_PROPERTIES
import org.jetbrains.skia.impl.Managed
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal abstract class Node {
    private var released: Boolean = false
    private val resourcesDisposer = mutableListOf<() -> Unit>()
    private val computedPropsUpdater = mutableMapOf<KProperty<*>, MutableList<() -> Unit>>()

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

                        thisRef.computedPropsUpdater.getOrElse(property, ::emptyList).forEach { it() }
                        thisRef.needRedraw()
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
                        }
                    }
                    return value
                }
            }

            if (managed) {
                thisRef.resourcesDisposer.add { computedProperty.getValue(thisRef, property) as? Managed }
            }

            deps.forEach { computedPropsUpdater.getOrPut(it, ::mutableListOf).add(computedProperty::invalidate) }
            computedProperty
        }
    }

    fun release() {
        resourcesDisposer.forEach { it.invoke() }
        released = true
    }

    fun needRedraw() {
        if (!released) {
            doNeedRedraw()
        }
    }

    protected open fun doNeedRedraw() {}
    protected open fun repr(): String? = null
}
