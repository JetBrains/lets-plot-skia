package org.jetbrains.letsPlot.skia.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.TextMetrics

class SkiaCanvasPeer : CanvasPeer {
    override fun createCanvas(size: Vector): Canvas {
        TODO("Not yet implemented")
    }

    override fun createSnapshot(bitmap: Bitmap): Canvas.Snapshot {
        TODO("Not yet implemented")
    }

    override fun decodeDataImageUrl(dataUrl: String): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }

    override fun decodePng(png: ByteArray): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }

    override fun measureText(text: String, font: Font): TextMetrics {
        TODO("Not yet implemented")
    }
}