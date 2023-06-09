/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.pane

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect


internal class Pane : Parent() {
    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var width: Float by visualProp(0.0f)
    var height: Float by visualProp(0.0f)

    override fun doDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(x, y)
        super.doDraw(canvas)
        canvas.restore()
    }

    override val localBounds: Rect
        get() = Rect.makeXYWH(x, y, width, height)
}