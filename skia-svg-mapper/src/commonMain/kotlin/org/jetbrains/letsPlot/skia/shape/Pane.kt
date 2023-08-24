/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.skia.Matrix33


internal class Pane : Parent() {
    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var width: Float by visualProp(0.0f)
    var height: Float by visualProp(0.0f)

    private val localTranslate: Matrix33 by dependencyProp(Pane::x, Pane::y) {
        Matrix33.makeTranslate(x, y)
    }

    override val localTransform: Matrix33
        get() = (transform ?: Matrix33.IDENTITY).makeConcat(localTranslate)
}
