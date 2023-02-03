/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia.mapper.drawing

import org.jetbrains.skia.Rect
import kotlin.math.max
import kotlin.math.min

internal class Group: Parent() {
    override val offsetX: Float get() = translateX
    override val offsetY: Float get() = translateY

    override fun doGetBounds(): Rect {
        return children.fold<Element, Rect?>(null) { acc, element ->
            if (acc != null) {
                Rect.makeLTRB(
                    min(acc.left, element.bounds.left),
                    min(acc.top, element.bounds.top),
                    max(acc.right, element.bounds.right),
                    max(acc.bottom, element.bounds.bottom)
                )
            } else {
                element.bounds
            }
        } ?: Rect.makeWH(0.0f, 0.0f)
    }
}
