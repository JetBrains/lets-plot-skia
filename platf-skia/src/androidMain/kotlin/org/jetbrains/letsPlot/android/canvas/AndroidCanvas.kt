/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.android.canvas

import android.graphics.Bitmap
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import kotlin.math.roundToInt

class AndroidCanvas(
    val bitmap: Bitmap,
    override val size: Vector,
    private val pixelDensity: Double
) : Canvas {
    companion object {
        fun create(size: Vector, pixelDensity: Double): AndroidCanvas {
            val s = if (size == Vector.ZERO) {
                Vector(1, 1)
            } else size

            val bitmap = Bitmap.createBitmap(
                (s.x * pixelDensity).roundToInt(),
                (s.y * pixelDensity).roundToInt(),
                Bitmap.Config.ARGB_8888
            )

            return AndroidCanvas(bitmap, s, pixelDensity)
        }
    }


    override val context2d: Context2d = AndroidContext2d(bitmap, pixelDensity)

    override fun immidiateSnapshot(): Canvas.Snapshot {
        TODO("Not yet implemented")
    }

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }
}