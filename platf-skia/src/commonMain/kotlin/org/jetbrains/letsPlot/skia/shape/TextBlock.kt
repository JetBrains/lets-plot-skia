/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.skia.mapping.svg.FontManager
import org.jetbrains.letsPlot.skia.shape.Text.Companion.DEFAULT_FONT_SIZE
import org.jetbrains.letsPlot.skia.shape.Text.HorizontalAlignment
import org.jetbrains.letsPlot.skia.shape.Text.VerticalAlignment
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Color4f
import org.jetbrains.skia.FontStyle
import kotlin.reflect.KProperty

internal class TextBlock(
    private val fontManager: FontManager
) : Container() {
    var textOrigin: VerticalAlignment? by visualProp(null)
    var textAlignment: HorizontalAlignment? by visualProp(null)
    var x: Float by visualProp(0f)
    var y: Float by visualProp(0f)

    var stroke: Color4f? by visualProp(null)
    var strokeWidth: Float by visualProp(1f)
    var strokeOpacity: Float by visualProp(1f)
    var strokeDashArray: List<Float>? by visualProp(null)
    var strokeMiter: Float? by visualProp(null) // not mandatory, default works fine

    var fill: Color4f? by visualProp(Color4f(Color.BLACK))
    var fillOpacity: Float by visualProp(1f)

    var fontFamily: List<String> by visualProp(emptyList())
    var fontStyle: FontStyle by visualProp(FontStyle.NORMAL)
    var fontSize by visualProp(DEFAULT_FONT_SIZE)

    var needLayout = true

    private val typeface by computedProp(TextBlock::fontFamily, TextBlock::fontStyle) {
        fontManager.matchFamiliesStyle(fontFamily, fontStyle)
    }

    private val font by computedProp(TextBlock::typeface, TextBlock::fontSize) {
        fontManager.font(typeface, fontSize)
    }

    private val lineHeight by computedProp(TextBlock::font) {
        font.metrics.descent - font.metrics.ascent
    }

    private val cx by computedProp(TextBlock::textAlignment) {
        val width = children.fold(0f) { acc, v -> acc + (v as TSpan).dim().first }

        when (textAlignment) {
            HorizontalAlignment.LEFT -> 0.0f
            HorizontalAlignment.CENTER -> -width / 2.0f
            HorizontalAlignment.RIGHT -> -width
            null -> 0.0f
        }
    }

    private val cy by computedProp(TextBlock::textOrigin, TextBlock::lineHeight) {
        // Vertical alignment should be computed without sub/super script, that's why we don't use textBlobInfo here
        when (textOrigin) {
            VerticalAlignment.TOP -> lineHeight * 0.74f
            VerticalAlignment.CENTER -> lineHeight * 0.37f
            null -> 0.0f
        }
    }

    override fun render(canvas: Canvas) {
        if (needLayout) {
            layoutChildren()
        }
    }

    fun layoutChildren() {
        var curX = 0f

        children.forEach {
            it as TSpan

            it.layoutX = x + cx + curX
            it.layoutY = y + cy

            curX += it.dim().first
        }

        needLayout = false
    }

    override fun onPropertyChanged(prop: KProperty<*>) {
        if (prop == TextBlock::x
            || prop == TextBlock::y
            || prop == TextBlock::textAlignment
            || prop == TextBlock::textOrigin)
        {
            needLayout = true
        }

        children.forEach { el ->
            el as TSpan
            when (prop) {
                TextBlock::fill -> el.fill = el.fill ?: fill
                TextBlock::stroke -> el.stroke =el.stroke ?: stroke
                TextBlock::strokeDashArray -> el.strokeDashArray = el.strokeDashArray ?: strokeDashArray
                TextBlock::fontFamily -> fontFamily.takeIf { el.fontFamily.isEmpty() }?.let { el.fontFamily = it }
                TextBlock::fontStyle -> el.fontStyle = fontStyle
                TextBlock::fontSize -> el.fontSize = fontSize
                TextBlock::strokeWidth -> el.strokeWidth = strokeWidth
                TextBlock::strokeOpacity -> el.strokeOpacity = strokeOpacity
            }
        }
    }

    override fun onChildAdded(event: CollectionItemEvent<out Element>) {
        val el = event.newItem as TSpan
        el.fill = el.fill ?: fill
        el.stroke = el.stroke ?: stroke
        el.strokeWidth = strokeWidth
        el.strokeOpacity = strokeOpacity
        el.strokeDashArray = el.strokeDashArray ?: strokeDashArray
        el.fontFamily = el.fontFamily.takeIf { it.isNotEmpty()  } ?: fontFamily
        el.fontStyle = fontStyle
        el.fontSize = fontSize
    }
}
