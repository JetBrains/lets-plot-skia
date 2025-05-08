package org.jetbrains.letsPlot.android.canvas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import org.jetbrains.letsPlot.android.canvas.Utils.toAndroidColor
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.ContextStateDelegate
import org.jetbrains.letsPlot.core.canvas.LineCap
import org.jetbrains.letsPlot.core.canvas.LineJoin

class AndroidContext2d(
    bitmap: Bitmap,
    private val stateDelegate: ContextStateDelegate = ContextStateDelegate(failIfNotImplemented = false, logEnabled = false),
) : Context2d by stateDelegate {
    private val nativeCanvas = Canvas(bitmap)

    private val strokePaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val fillPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var currentPath: Path? = null

    override fun save() {
        stateDelegate.save()
        nativeCanvas.save()
    }

    override fun restore() {
        nativeCanvas.restore()
        stateDelegate.restore()
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        nativeCanvas.drawRect(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat(), fillPaint)
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        nativeCanvas.drawRect(x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat(), strokePaint)
    }

    override fun fillText(text: String, x: Double, y: Double) {
        nativeCanvas.drawText(text, x.toFloat(), y.toFloat(), fillPaint)
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        nativeCanvas.drawText(text, x.toFloat(), y.toFloat(), strokePaint)
    }

    override fun beginPath() {
        currentPath = Path()
        stateDelegate.beginPath()
    }

    override fun moveTo(x: Double, y: Double) {
        currentPath?.moveTo(x.toFloat(), y.toFloat())
        stateDelegate.moveTo(x, y)
    }

    override fun lineTo(x: Double, y: Double) {
        currentPath?.lineTo(x.toFloat(), y.toFloat())
        stateDelegate.lineTo(x, y)
    }

    override fun closePath() {
        currentPath?.close()
        stateDelegate.closePath()
    }

    override fun stroke() {
        nativeCanvas.drawPath(currentPath!!, strokePaint)
    }

    override fun fill() {
        nativeCanvas.drawPath(currentPath!!, fillPaint)
    }

    override fun setFillStyle(color: Color?) {
        fillPaint.color = color?.toAndroidColor() ?: 0
    }

    override fun setStrokeStyle(color: Color?) {
        strokePaint.color = color?.toAndroidColor() ?: 0
    }

    override fun rotate(angle: Double) {
        stateDelegate.rotate(angle)
        nativeCanvas.rotate(angle.toFloat())
    }

    override fun translate(x: Double, y: Double) {
        stateDelegate.translate(x, y)
        nativeCanvas.translate(x.toFloat(), y.toFloat())
    }

    override fun scale(x: Double, y: Double) {
        stateDelegate.scale(x, y)
        nativeCanvas.scale(x.toFloat(), y.toFloat())
    }

    override fun scale(xy: Double) {
        stateDelegate.scale(xy)
        nativeCanvas.scale(xy.toFloat(), xy.toFloat())
    }

    override fun setTransform(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double) {
        stateDelegate.setTransform(m00, m10, m01, m11, m02, m12)
        nativeCanvas.setMatrix(android.graphics.Matrix().apply {
            setValues(floatArrayOf(
                m00.toFloat(), m10.toFloat(), 0f,
                m01.toFloat(), m11.toFloat(), 0f,
                m02.toFloat(), m12.toFloat(), 1f
            ))
        })
    }

    override fun setLineWidth(lineWidth: Double) {
        strokePaint.strokeWidth = lineWidth.toFloat()
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        strokePaint.strokeJoin = when (lineJoin) {
            LineJoin.BEVEL -> Paint.Join.BEVEL
            LineJoin.MITER -> Paint.Join.MITER
            LineJoin.ROUND -> Paint.Join.ROUND
        }
    }

    override fun setLineCap(lineCap: LineCap) {
        strokePaint.strokeCap = when (lineCap) {
            LineCap.BUTT -> Paint.Cap.BUTT
            LineCap.ROUND -> Paint.Cap.ROUND
            LineCap.SQUARE -> Paint.Cap.SQUARE
        }
    }

    override fun setStrokeMiterLimit(miterLimit: Double) {
        strokePaint.strokeMiter = miterLimit.toFloat()
    }

}
