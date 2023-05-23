/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.pane

import org.jetbrains.skia.*

internal class Text : Figure() {
    var textOrigin: VerticalAlignment? by visualProp(null)
    var textAlignment: HorizontalAlignment? by visualProp(null)
    var x: Float by visualProp(0.0f)
    var y: Float by visualProp(0.0f)
    var text: String by visualProp("")
    var fontFamily: List<String> by visualProp(emptyList())
    var fontStyle: FontStyle by visualProp(FontStyle.NORMAL)
    var fontSize by visualProp(16.0f)

    private val cx by dependencyProp(Text::textLine, Text::textAlignment) {
        when (textAlignment) {
            HorizontalAlignment.LEFT -> 0.0f
            HorizontalAlignment.CENTER -> -textLine.width / 2.0f
            HorizontalAlignment.RIGHT -> -textLine.width
            null -> 0.0f
        }
    }

    private val cy by dependencyProp(Text::textLine, Text::textOrigin) {
        when (textOrigin) {
            VerticalAlignment.TOP -> textLine.xHeight
            VerticalAlignment.CENTER -> textLine.xHeight / 2.0f
            VerticalAlignment.BOTTOM -> -textLine.xHeight
            null -> 0.0f
        }
    }

    private val typeface by dependencyProp(Text::fontFamily, Text::fontStyle) {
        FontMgr.default.matchFamiliesStyle(fontFamily.toTypedArray(), fontStyle) ?: Typeface.makeDefault()
    }

    private val font by dependencyProp(Text::typeface, Text::fontSize) {
        Font(typeface, fontSize)
    }

    private val textLine by dependencyProp(Text::text, Text::font) {
        TextLine.make(text, font)
    }

    override fun doDraw(canvas: Canvas) {
        fillPaint?.let { canvas.drawTextLine(textLine, x + cx, y + cy, it) }
        strokePaint?.let { canvas.drawTextLine(textLine, x + cx, y + cy, it) }
    }

    override val localBounds: Rect
        get() = font.measureText(text).let { bbox ->
            Rect.makeLTRB(
                bbox.left + x + cx,
                bbox.top + y + cy,
                bbox.right + x + cx,
                bbox.bottom + y + cy
            )
        }

    override fun repr(): String = text

    enum class VerticalAlignment {
        TOP,
        CENTER,
        BOTTOM
    }

    enum class HorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT
    }
}
