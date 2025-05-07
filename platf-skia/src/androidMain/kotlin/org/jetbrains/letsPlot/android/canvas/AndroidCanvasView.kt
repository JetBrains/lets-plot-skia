package org.jetbrains.letsPlot.android.canvas

import android.content.Context
import android.graphics.Canvas
import android.view.View

class AndroidCanvasView(
    private val canvas: AndroidCanvas,
    context: Context,
) : View(context) {

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        canvas.drawBitmap(this.canvas.bitmap, 0f, 0f, null)
    }

}