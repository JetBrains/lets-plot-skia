/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.skia.Matrix33
import kotlin.reflect.KProperty


internal class Pane : Container() {
    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var width: Float by visualProp(0.0f)
    var height: Float by visualProp(0.0f)

    // implicitly converts x and y to translate transform. User should not change transform explicitly.
    override fun onPropertyChanged(prop: KProperty<*>) {
        when (prop) {
            Pane::x, Pane::y -> transform = Matrix33.makeTranslate(x, y)
        }
    }
}
