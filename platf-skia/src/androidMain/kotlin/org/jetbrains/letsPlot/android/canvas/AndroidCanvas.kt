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

class AndroidCanvas(
    val bitmap: Bitmap,
    override val size: Vector,
    private val pixelRatio: Double
) : Canvas {
    companion object {
        fun create(size: Vector, pixelRatio: Double): AndroidCanvas {
            val s = if (size == Vector.ZERO) {
                Vector(1, 1)
            } else size

            return AndroidCanvas(Bitmap.createBitmap(s.x, s.y, Bitmap.Config.ARGB_8888), s, pixelRatio)
        }
    }


    override val context2d: Context2d = AndroidContext2d(bitmap)

    override fun immidiateSnapshot(): Canvas.Snapshot {
        TODO("Not yet implemented")
    }

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }
}