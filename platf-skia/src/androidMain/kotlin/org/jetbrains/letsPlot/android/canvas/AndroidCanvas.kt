/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.android.canvas

import android.graphics.Bitmap
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import kotlin.math.roundToInt

class AndroidCanvas(
    val bitmap: Bitmap,
    override val size: Vector,
    pixelDensity: Double
) : Canvas {
    companion object {
        fun create(size: Vector, pixelDensity: Double): AndroidCanvas {
            val w = (size.x * pixelDensity).roundToInt().coerceAtLeast(1)
            val h = (size.y * pixelDensity).roundToInt().coerceAtLeast(1)

            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

            return AndroidCanvas(bitmap, Vector(w, h), pixelDensity)
        }
    }

    override val context2d: Context2d = AndroidContext2d(bitmap, pixelDensity)

    override fun immidiateSnapshot(): Canvas.Snapshot {
        return AndroidSnapshot(bitmap.copy(this.bitmap.config, false))
    }

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        return Asyncs.constant(immidiateSnapshot())
    }

    class AndroidSnapshot(
        val bitmap: Bitmap
    ) : Canvas.Snapshot {
        override fun copy(): Canvas.Snapshot {
            val newBitmap = bitmap.copy(bitmap.config, false)
            return AndroidSnapshot(newBitmap)
        }
    }
}