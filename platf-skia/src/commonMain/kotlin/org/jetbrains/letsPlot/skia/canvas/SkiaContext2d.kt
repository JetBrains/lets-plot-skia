package org.jetbrains.letsPlot.skia.canvas

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.*
import org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot
import org.jetbrains.skia.*
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color.TRANSPARENT

class SkiaContext2d(
    val platformCanvas: Canvas,
    private val skiaFontManager: SkiaFontManager,
    private val contextState: ContextStateDelegate = ContextStateDelegate(failIfNotImplemented = false, logEnabled = true),
) : Context2d by contextState {

    private val strokePaint = Paint().apply {
        setStroke(true)
        isAntiAlias = true
    }

    private val fillPaint = Paint().apply {
        isAntiAlias = true
    }

    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        color = TRANSPARENT
    }

    override fun drawImage(snapshot: Snapshot) {
        //require(snapshot is AndroidSnapshot) { "Snapshot must be of type AndroidSnapshot" }
        //platformCanvas.drawBitmap(snapshot.platformBitmap, 0f, 0f, null)
    }

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double) {
        //require(snapshot is AndroidSnapshot) { "Snapshot must be of type AndroidSnapshot" }
        //platformCanvas.drawBitmap(snapshot.platformBitmap, x.toFloat(), y.toFloat(), null)
    }

    override fun drawImage(snapshot: Snapshot, x: Double, y: Double, dw: Double, dh: Double) {
        //require(snapshot is AndroidSnapshot) { "Snapshot must be of type AndroidSnapshot" }
        //val dstRect = Rect(x.toInt(), y.toInt(), (x + dw).toInt(), (y + dh).toInt())
        //platformCanvas.drawBitmap(snapshot.platformBitmap, null, dstRect, null)
    }

    override fun drawImage(snapshot: Snapshot, sx: Double, sy: Double, sw: Double, sh: Double, dx: Double, dy: Double, dw: Double, dh: Double) {
        //require(snapshot is AndroidSnapshot) { "Snapshot must be of type AndroidSnapshot" }
        //val srcRect = Rect(sx.toInt(), sy.toInt(), (sx + sw).toInt(), (sy + sh).toInt())
        //val dstRect = Rect(dx.toInt(), dy.toInt(), (dx + dw).toInt(), (dy + dh).toInt())
        //platformCanvas.drawBitmap(snapshot.platformBitmap, srcRect, dstRect, null)
    }

    override fun save() {
        contextState.save()
        platformCanvas.save()
    }

    override fun restore() {
        contextState.restore()
        platformCanvas.restore()
    }

    override fun rotate(angle: Double) {
        contextState.rotate(angle)
        platformCanvas.rotate(angle.toFloat())
    }

    override fun translate(x: Double, y: Double) {
        contextState.translate(x, y)
        platformCanvas.translate(x.toFloat(), y.toFloat())
    }

    override fun transform(sx: Double, ry: Double, rx: Double, sy: Double, tx: Double, ty: Double) {
        contextState.transform(sx = sx, ry = ry, rx = rx, sy = sy, tx = tx, ty = ty)
        platformCanvas.concat(Matrix33(
                sx.toFloat(), rx.toFloat(), tx.toFloat(),
                ry.toFloat(), sy.toFloat(), ty.toFloat(),
                0f, 0f, 1f
            )
        )
    }

    override fun scale(x: Double, y: Double) {
        contextState.scale(x, y)
        platformCanvas.scale(x.toFloat(), y.toFloat())
    }

    override fun scale(xy: Double) {
        contextState.scale(xy)
        platformCanvas.scale(xy.toFloat(), xy.toFloat())
    }

    override fun setTransform(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double) {
        contextState.setTransform(m00, m10, m01, m11, m02, m12)
        platformCanvas.setMatrix(Matrix33(
                m00.toFloat(), m10.toFloat(), 0f,
                m01.toFloat(), m11.toFloat(), 0f,
                m02.toFloat(), m12.toFloat(), 1f
        )
        )
    }

    override fun clearRect(rect: DoubleRectangle) {
        platformCanvas.drawRect(skiaRectFromDoubleRectangle(rect), backgroundPaint)
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        platformCanvas.drawRect(skiaRectFromXYWH(x, y, w, h), fillPaint)
    }

    override fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        platformCanvas.drawRect(skiaRectFromXYWH(x, y, w, h), strokePaint)
    }

    override fun fillText(text: String, x: Double, y: Double) {
        drawText(text, x, y, fillPaint)
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        drawText(text, x, y, strokePaint)
    }

    private fun drawText(text: String, x: Double, y: Double, paint: Paint) {
        val skiaFont = skiaFontManager.findFont(contextState.getFont())

        val textBlob = TextBlobBuilder()
            .appendRun(skiaFont, text, 0f, 0f)
            .build()

        if (textBlob == null) {
            // No glyphs to draw (e.g., empty string)
            return
        }

        platformCanvas.drawTextBlob(textBlob, x.toFloat(), y.toFloat(), paint)
    }

    override fun measureText(str: String): TextMetrics {
        val skiaFont = skiaFontManager.findFont(contextState.getFont())
        val r = skiaFont.measureText(str, fillPaint)

        val textMetrics = TextMetrics(
            ascent = r.top.toDouble(),
            descent = r.bottom.toDouble(),
            bbox = DoubleRectangle.XYWH(
                x = r.left,
                y = r.top,
                width = r.width,
                height = r.height
            ),
        )

        return textMetrics
    }

    override fun setLineDash(lineDash: DoubleArray) {
        contextState.setLineDash(lineDash)
        setupPathEffect()
    }

    override fun setLineDashOffset(lineDashOffset: Double) {
        contextState.setLineDashOffset(lineDashOffset)
        setupPathEffect()
    }

    private fun setupPathEffect() {
        val lineDash = contextState.getLineDash().map(Double::toFloat).toFloatArray()
        if (lineDash.isEmpty()) {
            strokePaint.pathEffect = null
            return
        }

        val phase = contextState.getLineDashOffset().toFloat()

        strokePaint.pathEffect = PathEffect.makeDash(lineDash, phase)
    }


    override fun stroke() {
        // Make ctm identity. null for degenerate case, e.g., scale(0, 0) - skip drawing.
        val inverseCtmTransform = contextState.getCTM().inverse() ?: return

        platformCanvas.drawPath(drawPath(contextState.getCurrentPath(), inverseCtmTransform), strokePaint)
    }

    override fun fill() {
        // Make ctm identity. null for degenerate case, e.g., scale(0, 0) - skip drawing.
        val inverseCtmTransform = contextState.getCTM().inverse() ?: return
        platformCanvas.drawPath(drawPath(contextState.getCurrentPath(), inverseCtmTransform), fillPaint)
    }

    override fun fillEvenOdd() {
        // Make ctm identity. null for degenerate case, e.g., scale(0, 0) - skip drawing.
        val inverseCtmTransform = contextState.getCTM().inverse() ?: return

        val path = drawPath(contextState.getCurrentPath(), inverseCtmTransform)
        path.fillMode = org.jetbrains.skia.PathFillMode.EVEN_ODD
        platformCanvas.drawPath(path, fillPaint)
    }

    override fun setFillStyle(color: Color?) {
        fillPaint.color = skiaIntFromColor(color)
    }

    override fun setStrokeStyle(color: Color?) {
        strokePaint.color = skiaIntFromColor(color)
    }

    override fun setLineWidth(lineWidth: Double) {
        strokePaint.strokeWidth = lineWidth.toFloat()
    }

    override fun setLineJoin(lineJoin: LineJoin) {
        strokePaint.strokeJoin = when (lineJoin) {
            LineJoin.BEVEL -> PaintStrokeJoin.BEVEL
            LineJoin.MITER -> PaintStrokeJoin.MITER
            LineJoin.ROUND -> PaintStrokeJoin.ROUND
        }
    }

    override fun setLineCap(lineCap: LineCap) {
        strokePaint.strokeCap = when (lineCap) {
            LineCap.BUTT -> PaintStrokeCap.BUTT
            LineCap.ROUND -> PaintStrokeCap.ROUND
            LineCap.SQUARE -> PaintStrokeCap.SQUARE
        }
    }

    override fun setStrokeMiterLimit(miterLimit: Double) {
        strokePaint.strokeMiter = miterLimit.toFloat()
    }

    override fun measureTextWidth(str: String): Double {
        return measureText(str).bbox.width
    }

    override fun clip() {
        // Make ctm identity. null for degenerate case, e.g., scale(0, 0) - skip drawing.
        val inverseCtmTransform = contextState.getCTM().inverse() ?: return

        val path = drawPath(contextState.getCurrentPath(), inverseCtmTransform)
        platformCanvas.clipPath(path)
    }

    private fun drawPath(commands: List<Path2d.PathCommand>, transform: AffineTransform): org.jetbrains.skia.Path {
        if (commands.isEmpty()) {
            return org.jetbrains.skia.Path()
        }

        val path = org.jetbrains.skia.Path()

        commands
            .asSequence()
            .map { cmd -> cmd.transform(transform) }
            .forEach { cmd ->
                when (cmd) {
                    is Path2d.MoveTo -> path.moveTo(cmd.x.toFloat(), cmd.y.toFloat())
                    is Path2d.LineTo -> path.lineTo(cmd.x.toFloat(), cmd.y.toFloat())
                    is Path2d.CubicCurveTo -> {
                        cmd.controlPoints.asSequence()
                            .windowed(size = 3, step = 3)
                            .forEach { (cp1, cp2, cp3) ->
                                path.cubicTo(
                                    cp1.x.toFloat(), cp1.y.toFloat(),
                                    cp2.x.toFloat(), cp2.y.toFloat(),
                                    cp3.x.toFloat(), cp3.y.toFloat()
                                )
                            }
                    }

                    is Path2d.ClosePath -> path.close()
                }
            }

        return path
    }


    companion object {
        private fun skiaRectFromDoubleRectangle(rect: DoubleRectangle): Rect {
            return Rect(
                rect.left.toFloat(),
                rect.top.toFloat(),
                rect.right.toFloat(),
                rect.bottom.toFloat()
            )
        }

        private fun skiaRectFromXYWH(x: Double, y: Double, w: Double, h: Double): Rect {
            return Rect(
                x.toFloat(),
                y.toFloat(),
                (x + w).toFloat(),
                (y + h).toFloat()
            )
        }

        internal fun skiaIntFromColor(color: Color?, def: Int = 0) : Int {
            if (color == null) {
                return def
            }

            return Color4f(
                r = (color.red / 255.0).toFloat(),
                g = (color.green / 255.0).toFloat(),
                b = (color.blue / 255.0).toFloat(),
                a = (color.alpha / 255.0).toFloat()
            ).toColor()
        }
    }
}
