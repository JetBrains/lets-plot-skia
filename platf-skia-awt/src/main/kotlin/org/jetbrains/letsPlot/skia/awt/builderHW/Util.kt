/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.awt.builderHW

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import java.awt.Rectangle

internal fun toAwtRect(from: DoubleRectangle): Rectangle {
    return Rectangle(
        from.origin.x.toInt(),
        from.origin.y.toInt(),
        (from.dimension.x + 0.5).toInt(),
        (from.dimension.y + 0.5).toInt()
    )
}
