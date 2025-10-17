package org.jetbrains.letsPlot.skia.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d

class SkiaCanvas : Canvas {
    override val context2d: Context2d
        get() = TODO("Not yet implemented")
    override val size: Vector
        get() = TODO("Not yet implemented")

    override fun takeSnapshot(): Canvas.Snapshot {
        TODO("Not yet implemented")
    }
}