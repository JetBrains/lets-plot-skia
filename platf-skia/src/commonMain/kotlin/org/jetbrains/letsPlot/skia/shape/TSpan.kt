/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.skia.mapping.svg.FontManager
import org.jetbrains.letsPlot.skia.shape.Text.BaselineShift
import org.jetbrains.skia.*
import kotlin.reflect.KProperty

internal class TSpan(
    private val fontManager: FontManager
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
    var fontSize by visualProp(Text.DEFAULT_FONT_SIZE)

    var layoutX: Float by visualProp(0.0f)
    var layoutY: Float by visualProp(0.0f)

    private val typeface by computedProp(TSpan::fontFamily, TSpan::fontStyle) {
        fontManager.matchFamiliesStyle(fontFamily, fontStyle)
    }

    private val font by computedProp(TSpan::typeface, TSpan::fontSize) {
        fontManager.font(typeface, fontSize)
    }

    private val lineHeight by computedProp(TSpan::font) {
        font.metrics.descent - font.metrics.ascent
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

    private val textData by computedProp(
        TSpan::text,
        TSpan::baselineShift,
        TSpan::dy,
        TSpan::fontScale,
        TSpan::font,
        TSpan::lineHeight
    ) {
        if (text.isEmpty()) return@computedProp null

        val blobBuilder = TextBlobBuilder()
        val glyphs = font.getStringGlyphs(text)
        val glyphXPos = font.getXPositions(glyphs)
        val xPosRange = (glyphXPos.firstOrNull() ?: 0f)..(glyphXPos.lastOrNull() ?: 0f)
        fun scale(x: Float): Float = (x - xPosRange.start) * fontScale + xPosRange.start

        val rsxTransforms = glyphXPos.map {
            RSXform.makeFromRadians(
                scale = fontScale,
                radians = 0f,
                tx = scale(it),
                ty = -(baselineShift.percent * lineHeight) + lineHeight * dy,
                ax = 0f,
                ay = 0f
            )
        }

        blobBuilder.appendRunRSXform(font, glyphs, rsxTransforms.toTypedArray())
        val blob = blobBuilder.build() ?: return@computedProp null

        // font.measureText ignores trailing spaces, so we need to use measureTextWidth
        val measuredTextWidth = font.measureTextWidth(text)

        // Adjust bbox from skia (to not calculate it manually)
        val bbox = font.measureText(text).let {
            Rect.makeXYWH(
                l = it.left,
                t = it.top - baselineShift.percent * lineHeight,
                w = measuredTextWidth * fontScale,
                h = it.height * fontScale
            )
        }

        TextData(
            blob,
            left = bbox.left,
            top = bbox.top,
            width = bbox.width,
            height = bbox.height,
        )
    }

    override fun render(canvas: Canvas) {
        val blob = textData?.blob ?: return

        styleData.fillPaint?.let { canvas.drawTextBlob(blob, layoutX, layoutY, it) }
        styleData.strokePaint?.let { canvas.drawTextBlob(blob, layoutX, layoutY, it) }
    }

    fun measure(): Pair<Float, Float> {
        val width = textData?.width ?: 0f
        val height = textData?.height ?: 0f
        return width to height
    }

    private class TextData(
        val blob: TextBlob,
        val left: Float,
        val top: Float,
        val width: Float,
        val height: Float,
    ) {
        val right = left + width
        val bottom = top + height
        val dim = Pair(width, height)
    }

    private class StyleData(
        val fillPaint: Paint?,
        val strokePaint: Paint?,
    )

    override val localBounds: Rect
        get() {
            val textData = textData ?: return Rect.makeWH(0.0f, 0.0f)

            val left = textData.left
            val top = textData.top
            val right = textData.right
            val bottom = textData.bottom

            return Rect.makeLTRB(
                layoutX + left,
                layoutY + top,
                layoutX + right,
                layoutY + bottom
            )
        }

    override fun onPropertyChanged(prop: KProperty<*>) {
        if (prop == TSpan::textData) {
            (parent as? Text)?.invalidateLayout()
        }
    }

    override fun repr(): String? {
        return ", text: \"$text\"" + super.repr()
    }
}
