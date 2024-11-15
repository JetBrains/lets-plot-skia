/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.skia.mapping.svg.FontManager
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Color4f
import org.jetbrains.skia.FontStyle
import kotlin.reflect.KProperty

internal class Text(
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

    private var needLayout = true

    private val typeface by computedProp(Text::fontFamily, Text::fontStyle) {
        fontManager.matchFamiliesStyle(fontFamily, fontStyle)
    }

    private val font by computedProp(Text::typeface, Text::fontSize) {
        fontManager.font(typeface, fontSize)
    }

    private val lineHeight by computedProp(Text::font) {
        font.metrics.descent - font.metrics.ascent
    }

    private val cx by computedProp(Text::textAlignment) {
        fun contentWidth(): Float =
            children
                .map { (it as TSpan).measure() }
                .fold(0f) { overallWidth, (w, _) -> overallWidth + w }

        when (textAlignment) {
            HorizontalAlignment.LEFT -> 0.0f
            HorizontalAlignment.CENTER -> -contentWidth() / 2.0f
            HorizontalAlignment.RIGHT -> -contentWidth()
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

            curX += it.measure().first
        }

        needLayout = false
    }

    internal fun invalidateLayout() {
        needLayout = true
    }

    override fun onPropertyChanged(prop: KProperty<*>) {
        if (prop == Text::x
            || prop == Text::y
            || prop == Text::textAlignment
            || prop == Text::textOrigin
        ) {
            invalidateLayout()
        }

        children.forEach { el ->
            el as TSpan
            when (prop) {
                Text::fill -> el.inheritValue(TSpan::fill, fill)
                Text::stroke -> el.inheritValue(TSpan::stroke, stroke)
                Text::strokeDashArray -> el.inheritValue(TSpan::strokeDashArray, strokeDashArray)
                Text::fontFamily -> el.inheritValue(TSpan::fontFamily, fontFamily)
                Text::fontStyle -> el.inheritValue(TSpan::fontStyle, fontStyle)
                Text::fontSize -> el.inheritValue(TSpan::fontSize, fontSize)
                Text::strokeWidth -> el.inheritValue(TSpan::strokeWidth, strokeWidth)
                Text::strokeOpacity -> el.inheritValue(TSpan::strokeOpacity, strokeOpacity)
            }
        }
    }

    override fun onChildAdded(event: CollectionItemEvent<out Element>) {
        val el = event.newItem as TSpan
        el.inheritValue(TSpan::fill, fill)
        el.inheritValue(TSpan::stroke, stroke)
        el.inheritValue(TSpan::strokeWidth, strokeWidth)
        el.inheritValue(TSpan::strokeOpacity, strokeOpacity)
        el.inheritValue(TSpan::strokeDashArray, strokeDashArray)
        el.inheritValue(TSpan::fontFamily, fontFamily)
        el.inheritValue(TSpan::fontStyle, fontStyle)
        el.inheritValue(TSpan::fontSize, fontSize)

        invalidateLayout()
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

    enum class BaselineShift(
        val percent: Float
    ) {
        SUB(-0.4f),
        SUPER(0.4f),
        NONE(0f)
    }

    companion object {
        const val DEFAULT_FONT_SIZE: Float = 16f
        val DEFAULT_FONT_FAMILY: List<String> = emptyList()
    }
}
