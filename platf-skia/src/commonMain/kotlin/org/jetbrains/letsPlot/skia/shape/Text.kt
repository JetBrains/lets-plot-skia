/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.skia.mapping.svg.FontManager
import org.jetbrains.skia.*


// Single line text
internal class Text(
    private val fontManager: FontManager
) : Figure() {

    var textOrigin: VerticalAlignment? by visualProp(null)
    var textAlignment: HorizontalAlignment? by visualProp(null)
    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var content: List<TextRun> by visualProp(emptyList())
    var fontFamily: List<String> by visualProp(emptyList())
    var fontStyle: FontStyle by visualProp(FontStyle.NORMAL)
    var fontSize by visualProp(DEFAULT_FONT_SIZE)

    private val typeface by computedProp(Text::fontFamily, Text::fontStyle) {
        fontManager.matchFamiliesStyle(fontFamily, fontStyle)
    }

    private val font by computedProp(Text::typeface, Text::fontSize) {
        fontManager.font(typeface, fontSize)
    }

    private val lineHeight by computedProp(Text::font) {
        font.metrics.descent - font.metrics.ascent
    }

    private val styleData: List<StyleData> by computedProp(Text::content, Figure::fill, Figure::stroke, Figure::strokeWidth) {
        content.map { textRun ->
            StyleData(
                fillPaint = fillPaint(textRun.fill ?: fill),
                strokePaint = strokePaint(
                    stroke = textRun.stroke ?: stroke,
                    strokeWidth = textRun.strokeWidth ?: strokeWidth
                )
            )
        }
    }

    private val textData: List<TextData> by computedProp(Text::content, Text::font, Text::lineHeight) {
        if (content.isEmpty()) return@computedProp emptyList()
        if (content.all { it.text.isEmpty() }) return@computedProp emptyList()

        val bboxes = mutableListOf<Rect>()
        var currentPosX = 0f

        content.map { textRun ->
            val blobBuilder = TextBlobBuilder()
            val glyphs = font.getStringGlyphs(textRun.text)
            val glyphXPos = font.getXPositions(glyphs, currentPosX)
            val xPosRange = (glyphXPos.firstOrNull() ?: 0f)..(glyphXPos.lastOrNull() ?: 0f)
            fun scale(x: Float): Float = (x - xPosRange.start) * textRun.fontScale + xPosRange.start

            val rsxTransforms = glyphXPos.map {
                RSXform.makeFromRadians(
                    scale = textRun.fontScale,
                    radians = 0f,
                    tx = scale(it),
                    ty = -(textRun.baselineShift.percent * lineHeight) + lineHeight * textRun.dy,
                    ax = 0f,
                    ay = 0f
                )
            }

            blobBuilder.appendRunRSXform(font, glyphs, rsxTransforms.toTypedArray())

            // Adjust bbox from skia (to not calculate it manually)
            val bbox = font.measureText(textRun.text).let {
                Rect.makeXYWH(
                    l = it.left + currentPosX,
                    t = it.top - textRun.baselineShift.percent * lineHeight,
                    w = it.width * textRun.fontScale + widthCorrectionCoef,
                    h = it.height * textRun.fontScale
                )
            }
            bboxes.add(bbox)

            currentPosX += bbox.width

            TextData(
                blob = blobBuilder.build() ?: error("content is not empty, but textBlob is null"),
                left = bbox.left,
                top = bbox.top,
                width = bbox.width,
                height = bbox.height,
            )
        }
    }

    private val cx by computedProp(Text::textData, Text::textAlignment) {
        val width = textData.fold(0f) { acc, v -> acc + v.width }

        when (textAlignment) {
            HorizontalAlignment.LEFT -> 0.0f
            HorizontalAlignment.CENTER -> -width / 2.0f
            HorizontalAlignment.RIGHT -> -width
            null -> 0.0f
        }
    }

    private val cy by computedProp(Text::textOrigin, Text::lineHeight) {
        // Vertical alignment should be computed without sub/super script, that's why we don't use textBlobInfo here
        when (textOrigin) {
            VerticalAlignment.TOP -> lineHeight * 0.74f
            VerticalAlignment.CENTER -> lineHeight * 0.37f
            null -> 0.0f
        }
    }

    override fun render(canvas: Canvas) {
        textData.zip(styleData).forEach { (text, style) ->
            val textBlob = text.blob

            style.fillPaint?.let { canvas.drawTextBlob(textBlob, x + cx, y + cy, it) }
            style.strokePaint?.let { canvas.drawTextBlob(textBlob, x + cx, y + cy, it) }
        }
    }

    override val localBounds: Rect
        get() {
            val left = textData.minOf { it.left }
            val top = textData.minOf { it.top }
            val right = textData.maxOf { it.right }
            val bottom = textData.maxOf { it.bottom }
            return Rect.makeLTRB(
                x + cx + left,
                y + cy + top,
                x + cx + right,
                y + cy + bottom
            )
        }

    enum class VerticalAlignment {
        TOP,
        CENTER
    }

    enum class HorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    class TextRun(
        var text: String = "",
        var baselineShift: BaselineShift = BaselineShift.NONE,
        var dy: Float = 0f,
        var fontScale: Float = 1f,
        var fill: Color4f? = null,
        var stroke: Color4f? = null,
        var strokeWidth: Float? = null,
    )

    enum class BaselineShift(
        val percent: Float
    ) {
        SUB(-0.4f),
        SUPER(0.4f),
        NONE(0f)
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
    }

    private class StyleData(
        val fillPaint: Paint?,
        val strokePaint: Paint?,
    )

    private val widthCorrectionCoef = 0f

    companion object {
        const val DEFAULT_FONT_SIZE: Float = 16f
        val DEFAULT_FONT_FAMILY: List<String> = emptyList()
    }
}
