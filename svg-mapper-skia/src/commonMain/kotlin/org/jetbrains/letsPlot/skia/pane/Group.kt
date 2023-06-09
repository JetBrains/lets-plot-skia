/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.pane

import org.jetbrains.skia.Rect
import kotlin.math.max
import kotlin.math.min

internal class Group : Parent() {

    override val localBounds: Rect
        get() = children
            .filterNot { it is Parent && it.children.isEmpty() }
            .fold<Element, Rect?>(null) { acc, element ->
                if (acc != null) {
                    Rect.makeLTRB(
                        min(acc.left, element.localBounds.left),
                        min(acc.top, element.localBounds.top),
                        max(acc.right, element.localBounds.right),
                        max(acc.bottom, element.localBounds.bottom)
                    )
                } else {
                    element.localBounds
                }
            } ?: Rect.makeWH(0.0f, 0.0f)


    override val screenBounds: Rect
        get() {
            return children
                .filterNot { it is Parent && it.children.isEmpty() }
                .fold<Element, Rect?>(null) { acc, element ->
                    if (acc != null) {
                        element.screenBounds.let {
                            Rect.makeLTRB(
                                min(acc.left, it.left),
                                min(acc.top, it.top),
                                max(acc.right, it.right),
                                max(acc.bottom, it.bottom)
                            )
                        }
                    } else {
                        element.screenBounds
                    }
                } ?: Rect.makeWH(0.0f, 0.0f)
        }
}
