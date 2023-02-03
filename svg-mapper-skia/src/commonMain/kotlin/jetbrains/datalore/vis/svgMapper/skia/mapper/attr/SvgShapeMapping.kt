/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia.mapper.attr

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Color.Companion.BLACK
import jetbrains.datalore.vis.svg.SvgColors
import jetbrains.datalore.vis.svg.SvgConstants
import jetbrains.datalore.vis.svg.SvgShape
import jetbrains.datalore.vis.svgMapper.skia.asSkiaColor
import jetbrains.datalore.vis.svgMapper.skia.mapper.drawing.Figure
import jetbrains.datalore.vis.svgMapper.skia.namedColors
import org.jetbrains.skia.Color4f

internal abstract class SvgShapeMapping<TargetT : Figure> : SvgAttrMapping<TargetT>() {
    init {
//        target.smoothProperty().set(false)
//        target.strokeType = StrokeType.CENTERED
    }

    override fun setAttribute(target: TargetT, name: String, value: Any?) {
        when (name) {
            SvgShape.FILL.name -> setColor(value, fillGet(target), fillSet(target))
            SvgShape.FILL_OPACITY.name -> setOpacity(value!!.asFloat, fillGet(target), fillSet(target))
            SvgShape.STROKE.name -> setColor(value, strokeGet(target), strokeSet(target))
            SvgShape.STROKE_OPACITY.name -> setOpacity(value!!.asFloat, strokeGet(target), strokeSet(target))
            SvgShape.STROKE_WIDTH.name -> target.strokeWidth = value?.asFloat
            SvgConstants.SVG_STROKE_DASHARRAY_ATTRIBUTE -> {
                val strokeDashArray = (value as String).split(",").map(String::toFloat)
                target.strokeDashArray = strokeDashArray
            }
            else -> super.setAttribute(target, name, value)
        }
    }

    companion object {
        // This will reset fill color to black if color is defined via style
        private val fillGet = { shape: Figure -> { shape.fill ?: BLACK.asSkiaColor } }
        private val fillSet = { shape: Figure -> { c: Color4f? -> shape.fill = c } }
        // This will reset stroke color to black if color is defined via style
        private val strokeGet = { shape: Figure -> { shape.stroke ?: BLACK.asSkiaColor } }
        private val strokeSet = { shape: Figure -> { c: Color4f? -> shape.stroke = c } }


        /**
         * value : the color name (string) or SvgColor (jetbrains.datalore.vis.svg)
         */
        private fun setColor(value: Any?, get: () -> Color4f?, set: (Color4f?) -> Unit) {
            if (value == SvgColors.CURRENT_COLOR) return

            val newColor: Color? = when (value) {
                null -> null
                SvgColors.NONE -> null
                else -> {
                    val colorString = value.toString().lowercase()
                    namedColors[colorString]
                        ?: Color.parseOrNull(colorString)
                        ?: error("Unsupported color value: $colorString")
                }
            }
            set(newColor?.asSkiaColor?.withA(get()?.a ?: 1.0f))
        }



        private fun setOpacity(value: Float, get: () -> Color4f, set: (Color4f) -> Unit) {
            val c = get()
            set(c.withA(value))
        }
    }
}