/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.android.canvas

import android.graphics.Bitmap
import org.jetbrains.letsPlot.commons.geometry.Vector
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

    override fun takeSnapshot(): Canvas.Snapshot {
        return AndroidSnapshot(bitmap.copy(this.bitmap.config, false))
    }

    class AndroidSnapshot(
        val androidBitmap: Bitmap
    ) : Canvas.Snapshot {
        override val bitmap: org.jetbrains.letsPlot.commons.values.Bitmap
            get() {
                val argbInts = IntArray(androidBitmap.width * androidBitmap.height)
                androidBitmap.getPixels(argbInts, 0, androidBitmap.width, 0, 0, androidBitmap.width, androidBitmap.height)
                return org.jetbrains.letsPlot.commons.values.Bitmap(
                    androidBitmap.width,
                    androidBitmap.height,
                    argbInts
                )
            }

        override val size: Vector
            get() = Vector(androidBitmap.width, androidBitmap.height)

        override fun copy(): Canvas.Snapshot {
            val newBitmap = androidBitmap.copy(androidBitmap.config, false)
            return AndroidSnapshot(newBitmap)
        }
    }
}