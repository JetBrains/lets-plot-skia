/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.raster.shape.Text.BaselineShift
import kotlin.reflect.KProperty

internal class TSpan(
    private val textMeasure: (String, Font) -> Float
) : Figure() {
    init {
        isMouseTransparent = false // see Element::isMouseTransparent for details
    }
    var text: String by visualProp("")

    var baselineShift: BaselineShift by visualProp(BaselineShift.NONE)
    var dy: Float by visualProp(0f)
    var fontScale: Float by visualProp(1f)

    var fontFamily: List<String> by visualProp(emptyList())
    var fontStyle: FontStyle by visualProp(FontStyle.NORMAL)
    var fontWeight: FontWeight by visualProp(FontWeight.NORMAL)
    var fontSize by visualProp(Text.DEFAULT_FONT_SIZE)

    var layoutX: Float by visualProp(0f)
    var layoutY: Float by visualProp(0f)

    private val font by computedProp(TSpan::fontFamily, TSpan::fontWeight, TSpan::fontStyle, TSpan::fontSize) {
        Font(fontStyle, fontWeight, fontSize.toDouble(), fontFamily.firstOrNull() ?: "serif")
    }

    private val lineHeight by computedProp(TSpan::font) {
        font.fontSize//font.metrics.descent - font.metrics.ascent
    }

    private val styleData: StyleData by computedProp(
        Figure::fill,
        Figure::stroke,
        Figure::strokeWidth
    ) {
        StyleData(
            fillPaint = fillPaint(fill),
            strokePaint = strokePaint(stroke = stroke, strokeWidth = strokeWidth)
        )
    }

//    private val textData by computedProp(
//        TSpan::text,
//        TSpan::baselineShift,
//        TSpan::dy,
//        TSpan::fontScale,
//        TSpan::font,
//        TSpan::lineHeight
//    ) {
//        if (text.isEmpty()) return@computedProp null
//
//        val blobBuilder = TextBlobBuilder()
//        val glyphs = font.getStringGlyphs(text)
//        val glyphXPos = font.getXPositions(glyphs)
//        val xPosRange = (glyphXPos.firstOrNull() ?: 0f)..(glyphXPos.lastOrNull() ?: 0f)
//        fun scale(x: Float): Float = (x - xPosRange.start) * fontScale + xPosRange.start
//
//        val rsxTransforms = glyphXPos.map {
//            RSXform.makeFromRadians(
//                scale = fontScale,
//                radians = 0f,
//                tx = scale(it),
//                ty = -(baselineShift.percent * lineHeight) + lineHeight * dy,
//                ax = 0f,
//                ay = 0f
//            )
//        }
//
//        blobBuilder.appendRunRSXform(font, glyphs, rsxTransforms.toTypedArray())
//        val blob = blobBuilder.build() ?: return@computedProp null
//
//        // font.measureText ignores trailing spaces, so we need to use measureTextWidth
//        val measuredTextWidth = font.measureTextWidth(text)
//
//        // Adjust bbox from skia (to not calculate it manually)
//        val bbox = font.measureText(text).let {
//            DoubleRectangle.XYWH(
//                x = it.left,
//                y = it.top - baselineShift.percent * lineHeight,
//                width = measuredTextWidth * fontScale,
//                height = it.height * fontScale
//            )
//        }
//
//        TextData(
//            blob,
//            left = bbox.left,
//            top = bbox.top,
//            width = bbox.width,
//            height = bbox.height,
//        )
//    }

    override fun render(canvas: Canvas) {
        //val blob = textData?.blob ?: return

        styleData.fillPaint?.let {
            applyPaint(it, canvas)
            canvas.context2d.setFont(font)
            canvas.context2d.fillText(text, layoutX.toDouble(), layoutY.toDouble())
        }

        styleData.strokePaint?.let {
            canvas.context2d.setFont(font)
            canvas.context2d.strokeText(text, layoutX.toDouble(), layoutY.toDouble())
        }
    }

    fun measure(): Pair<Float, Float> {
        val width = textMeasure(text, font)//textData?.width ?: 0.0
        val height = fontSize//textData?.height ?: 0.0
        return width to height
    }

//    private class TextData(
//        val blob: TextBlob,
//        val left: Float,
//        val top: Float,
//        val width: Float,
//        val height: Float,
//    ) {
//        val right = left + width
//        val bottom = top + height
//        val dim = Pair(width, height)
//    }

    private class StyleData(
        val fillPaint: Paint?,
        val strokePaint: Paint?,
    )

    override val localBounds: DoubleRectangle
        get() {
            val (w, h) = measure()
            val textData = return DoubleRectangle.XYWH(0, 0, w, h)//textData ?: return DoubleRectangle.XYWH(0, 0, 0, 0)

//            val left = textData.left
//            val top = textData.top
//            val right = textData.right
//            val bottom = textData.bottom
//
//            return DoubleRectangle.LTRB(
//                layoutX + left,
//                layoutY + top,
//                layoutX + right,
//                layoutY + bottom
//            )
        }

    override fun onPropertyChanged(prop: KProperty<*>) {
        //if (prop == TSpan::textData) {
            (parent as? Text)?.invalidateLayout()
        //}
    }

    override fun repr(): String? {
        return ", text: \"$text\"" + super.repr()
    }
}
