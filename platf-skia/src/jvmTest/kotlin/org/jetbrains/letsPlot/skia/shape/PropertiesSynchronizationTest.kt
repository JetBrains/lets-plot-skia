/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import kotlin.test.Test
import kotlin.test.assertEquals

class PropertiesSynchronizationTest {

    @Test
    fun nestedDeps() {
        class C : Node() {
            var foo: String by visualProp("")
            val bar: String by computedProp(C::foo) { foo + "bar" }
            val baz: String by computedProp(C::bar) { bar + "baz" }
        }

        val c = C()
        assertEquals("barbaz", c.baz)

        c.foo = "foo"
        assertEquals("foobarbaz", c.baz)
    }

    @Test
    fun cyclicDepsWorInDefinitionOrder() {
        class C : Node() {
            var foo: String by visualProp("")
            val bar: String by computedProp(C::foo, C::baz) { foo + "bar" }
            val baz: String by computedProp(C::foo, C::bar) { bar + "baz" }
        }

        val c = C()
        assertEquals("", c.foo)
        assertEquals("bar", c.bar)
        assertEquals("barbaz", c.baz)
    }
}