/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia.mapper.drawing

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect

class Pane: Parent() {
    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var width: Float by visualProp(0.0f)
    var height: Float by visualProp(0.0f)
    override val offsetX: Float get() = translateX + x
    override val offsetY: Float get() = translateY + y

    override fun doDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(x, y)
        super.doDraw(canvas)
        canvas.restore()
    }

    override fun doGetBounds(): Rect {
        return Rect.makeXYWH(x, y, width, height).offset(absoluteOffsetX, absoluteOffsetY)
    }
}