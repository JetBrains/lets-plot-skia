/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svg

import demo.svg.utils.DemoBase
import demo.svg.utils.DemoWindow
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification.Companion.TextRotation
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification.Companion.applyJustification
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.FONT_FAMILY_NORMAL
import org.jetbrains.letsPlot.core.plot.builder.presentation.PlotLabelSpec
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

fun main() {
    with(TextJustificationDemo()) {
        DemoWindow(
            "Text justification",
            createSvgRoots(listOf(createModel())),
        ).open()
    }
}

private class TextJustificationDemo: DemoBase(DoubleVector(1600.0, 1200.0)) {

    override val cssStyle: String
        get() = ".$LABEL_CLASS_NAME { font-size: ${FONT_SIZE}px; }"

    fun createModel(): GroupComponent {
        val specs = List(11) { it.toDouble() / 10 }.map {
            TextJustification(it, it)
        }
        val rect = DoubleRectangle(
            DoubleVector(10.0, 10.0),
            DoubleVector(500.0, 80.0)
        )

        val groupComponent = GroupComponent()

        fun place(rotation: TextRotation?, startPos: DoubleVector) {
            var y = startPos.x
            var x = startPos.y
            specs.forEach { spec ->
                val labelExample = createLabelExample(rect, spec, rotation)
                SvgUtils.transformTranslate(labelExample, x, y)
                groupComponent.add(labelExample)
                if (rotation != null) {
                    x += 80.0
                } else {
                    y += rect.height + 20.0
                }
            }
        }

        place(rotation = null, startPos = DoubleVector(10.0, 10.0))
        place(rotation = TextRotation.CLOCKWISE, startPos = DoubleVector(10.0, 590.0))
        place(rotation = TextRotation.ANTICLOCKWISE, startPos = DoubleVector(590.0, 590.0))

        return groupComponent
    }

    companion object {
        private const val FONT_SIZE = 20.0
        private const val LABEL_CLASS_NAME = "label"

        private fun createLabelExample(
            rect: DoubleRectangle,
            justification: TextJustification,
            rotation: TextRotation?
        ): SvgGElement {
            val r = if (rotation != null) rect.flip() else rect
            val textLabel = createTextLabel(r, justification, rotation)
            val g = SvgGElement()
            g.children().add(createRect(r))
            g.children().add(textLabel.rootGroup)
            return g
        }

        private fun createRect(r: DoubleRectangle): SvgElement {
            val rect = SvgRectElement(r)
            rect.strokeColor().set(Color.DARK_BLUE)
            rect.strokeWidth().set(1.0)
            rect.fillOpacity().set(0.0)
            val g = SvgGElement()
            g.children().add(rect)
            return g
        }

        private fun createTextLabel(
            boundRect: DoubleRectangle,
            justification: TextJustification,
            rotation: TextRotation?
        ): MultilineLabel {
            val text = "Horizontal justification:" + justification.x + "\n" +
                    "Vertical justification:" + justification.y + "\n" +
                    "Angle: " + rotation.toString()

            val label = MultilineLabel(text)
            label.addClassName(LABEL_CLASS_NAME)
            label.textColor().set(Color.DARK_BLUE)

            val lineHeight = FONT_SIZE
            val lineFont = Font(FontFamily(FONT_FAMILY_NORMAL, false), FONT_SIZE.toInt())
            val textSize = DoubleVector(
                PlotLabelSpec(lineFont).width(text),
                lineHeight * label.linesCount()
            )
            val (position, hAnchor) = applyJustification(
                boundRect,
                textSize,
                lineHeight,
                justification,
                rotation
            )
            label.setLineHeight(lineHeight)
            label.setHorizontalAnchor(hAnchor)
            rotation?.angle?.let(label::rotate)
            label.moveTo(position)
            return label
        }
    }
}
