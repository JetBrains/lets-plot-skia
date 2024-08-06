/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.skia.*


// Single line text
internal class Text : Figure() {
    var textOrigin: VerticalAlignment? by visualProp(null)
    var textAlignment: HorizontalAlignment? by visualProp(null)
    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var content: List<TextRun> by visualProp(emptyList())
    var fontFamily: List<String> by visualProp(emptyList())
    var fontStyle: FontStyle by visualProp(FontStyle.NORMAL)
    var fontSize by visualProp(DEFAULT_FONT_SIZE)

    private val typeface by computedProp(Text::fontFamily, Text::fontStyle, managed = true) {
        FontMgr.default.matchFamiliesStyle(fontFamily.toTypedArray(), fontStyle) ?: Typeface.makeDefault()
    }

    private val font by computedProp(Text::typeface, Text::fontSize, managed = true) {
        Font(typeface, fontSize).apply { isSubpixel = true }
    }

    private val lineHeight by computedProp(Text::font) {
        font.metrics.descent - font.metrics.ascent
    }

    private val renderData: RenderData by computedProp(Text::content, Text::font, Text::lineHeight) {
        if (content.isEmpty()) return@computedProp RenderData.EMPTY
        if (content.all { it.text.isEmpty() }) return@computedProp RenderData.EMPTY

        val blobBuilder = TextBlobBuilder()
        val bboxes = mutableListOf<Rect>()
        var currentPosX = 0f

        content.forEach { textRun ->
            val scaleFactor = textRun.fontScale ?: 1f
            val baseline = when (textRun.baselineShift) {
                BaselineShift.SUPER -> lineHeight * 0.4f
                BaselineShift.SUB -> lineHeight * -0.4f
                else -> 0f
            }

            val glyphs = font.getStringGlyphs(textRun.text)
            val glyphXPos = font.getXPositions(glyphs, currentPosX)
            val xPosRange = (glyphXPos.firstOrNull() ?: 0f)..(glyphXPos.lastOrNull() ?: 0f)
            fun scale(x: Float): Float = (x - xPosRange.start) * scaleFactor + xPosRange.start

            val rsxTransforms = glyphXPos.map {
                RSXform.makeFromRadians(
                    scale = scaleFactor,
                    radians = 0f,
                    tx = scale(it),
                    ty = -baseline,
                    ax = 0f,
                    ay = 0f
                )
            }

            blobBuilder.appendRunRSXform(font, glyphs, rsxTransforms.toTypedArray())

            // Adjust bbox from skia (to not calculate it manually)
            val bbox = font.measureText(textRun.text).let {
                Rect.makeXYWH(
                    l = it.left + currentPosX,
                    t = it.top - baseline,
                    w = it.width * scaleFactor + widthCorrectionCoef,
                    h = it.height * scaleFactor
                )
            }
            bboxes.add(bbox)

            currentPosX += bbox.width
        }

        val textBlob = blobBuilder.build() ?: error("content is not empty, but textBlob is null")
        val overallBbox = union(bboxes) ?: error("content is not empty, but overallBbox is null")

        return@computedProp RenderData(
            textBlob = textBlob,
            left = overallBbox.left,
            top = overallBbox.top,
            width = overallBbox.width,
            height = overallBbox.height,
        )
    }

    private val cx by computedProp(Text::renderData, Text::textAlignment) {
        val width = renderData.width

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
        val textBlob = renderData.textBlob ?: return

        fillPaint?.let { canvas.drawTextBlob(textBlob, x + cx, y + cy, it) }
        strokePaint?.let { canvas.drawTextBlob(textBlob, x + cx, y + cy, it) }
    }

    override val localBounds: Rect
        get() = Rect.makeLTRB(
            x + cx + renderData.left,
            y + cy + renderData.top,
            x + cx + renderData.right,
            y + cy + renderData.bottom
        )

    enum class VerticalAlignment {
        TOP,
        CENTER
    }

    enum class HorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    data class TextRun(
        val text: String,
        val baselineShift: BaselineShift? = null,
        val fontScale: Float? = null,
    )

    enum class BaselineShift {
        SUB,
        SUPER
    }

    private class RenderData(
        val textBlob: TextBlob?,
        val left: Float,
        val top: Float,
        val width: Float,
        val height: Float,
    ) {
        val right = left + width
        val bottom = top + height

        companion object {
            val EMPTY = RenderData(null, 0f, 0f, 0f, 0f)
        }
    }

    private val widthCorrectionCoef = 0f

    companion object {
        const val DEFAULT_FONT_SIZE: Float = 16f
        val DEFAULT_FONT_FAMILY: List<String> = emptyList()
    }
}
