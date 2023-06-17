package org.jetbrains.letsPlot.skia.android

import android.content.Context
import android.util.TypedValue
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.Rectangle

object SizeConverter {

    fun boundsPxToDp(from: DoubleRectangle, ctx: Context): Rectangle {
        val x: Int = pxToDp(from.origin.x, ctx).toInt()
        val y: Int = pxToDp(from.origin.y, ctx).toInt()
        val w: Int = (pxToDp(from.width, ctx) + 0.5).toInt()
        val h: Int = (pxToDp(from.height, ctx) + 0.5).toInt()
        return Rectangle(x, y, w, h)
    }

    private fun pxToDp(v: Double, ctx: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, // unit
            v.toFloat(),  // value
            ctx.resources.displayMetrics // metrics
        )
    }
}