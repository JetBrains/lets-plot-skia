/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.skia.Bitmap

class SkSnapshot(
    val bitmap: Bitmap
) : Canvas.Snapshot {
    override fun copy(): Canvas.Snapshot {
        TODO()
    }
}