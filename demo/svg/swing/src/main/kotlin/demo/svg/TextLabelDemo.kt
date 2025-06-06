/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svg

import demo.svg.utils.DemoBase
import demo.svg.utils.DemoWindow
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.render.svg.TextLabel
import org.jetbrains.letsPlot.datamodel.svg.dom.*

fun main() {
    with(TextLabelDemo()) {
        DemoWindow(
            "Text label",
            createSvgRoots(listOf(createModel())),
        ).open()
    }
}

private class TextLabelDemo : DemoBase(DEMO_BOX_SIZE) {
    override val cssStyle: String
        get() = ".$LABEL_CLASS_NAME { font-size: 18px; }"

    fun createModel(): GroupComponent {
        val specs = ArrayList<LabelSpec>()
        specs.add(LabelSpec(Text.HorizontalAnchor.LEFT, Text.VerticalAnchor.BOTTOM, 0.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.MIDDLE, Text.VerticalAnchor.BOTTOM, 0.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.RIGHT, Text.VerticalAnchor.BOTTOM, 0.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.LEFT, Text.VerticalAnchor.CENTER, 0.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.MIDDLE, Text.VerticalAnchor.CENTER, 0.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.RIGHT, Text.VerticalAnchor.CENTER, 0.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.LEFT, Text.VerticalAnchor.TOP, 0.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.MIDDLE, Text.VerticalAnchor.TOP, 0.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.RIGHT, Text.VerticalAnchor.TOP, 0.0))

        specs.add(LabelSpec(Text.HorizontalAnchor.LEFT, Text.VerticalAnchor.BOTTOM, 30.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.MIDDLE, Text.VerticalAnchor.BOTTOM, 30.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.RIGHT, Text.VerticalAnchor.BOTTOM, 30.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.LEFT, Text.VerticalAnchor.CENTER, 30.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.MIDDLE, Text.VerticalAnchor.CENTER, 30.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.RIGHT, Text.VerticalAnchor.CENTER, 30.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.LEFT, Text.VerticalAnchor.TOP, 30.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.MIDDLE, Text.VerticalAnchor.TOP, 30.0))
        specs.add(LabelSpec(Text.HorizontalAnchor.RIGHT, Text.VerticalAnchor.TOP, 30.0))

        val groupComponent = GroupComponent()

        var exampleDim = DoubleVector(200.0, 50.0)
        var y = 50
        for ((i, spec) in specs.withIndex()) {
            if (i == 9) {
                exampleDim = exampleDim.add(DoubleVector(0.0, 50.0))
            }
            val labelExample = createLabelExample(
                exampleDim,
                spec.hAnchor,
                spec.vAnchor,
                spec.angle
            )
            SvgUtils.transformTranslate(labelExample, 300.0, y.toDouble())
            groupComponent.add(labelExample)
            y += exampleDim.y.toInt()
        }

        return groupComponent
    }

    private class LabelSpec(
        val hAnchor: Text.HorizontalAnchor,
        val vAnchor: Text.VerticalAnchor,
        val angle: Double
    )


    companion object {
        private val DEMO_BOX_SIZE = DoubleVector(800.0, 1500.0)
        private const val LABEL_CLASS_NAME = "label"

        private fun createLabelExample(
            dim: DoubleVector,
            hAnchor: Text.HorizontalAnchor,
            vAnchor: Text.VerticalAnchor,
            angle: Double
        ): SvgGElement {
            val axis = createAxis(dim)
            val textLabel = createTextLabel(
                hAnchor,
                vAnchor,
                angle
            )
            textLabel.moveTo(dim.x / 2, dim.y / 2)

            val g = SvgGElement()
            g.children().add(axis)
            g.children().add(textLabel.rootGroup)
            return g
        }

        private fun createAxis(dim: DoubleVector): SvgElement {
            val hAxis = SvgLineElement(0.0, dim.y / 2, dim.x, dim.y / 2)
            hAxis.stroke().set(SvgColors.RED)
            val vAxis = SvgLineElement(dim.x / 2, 0.0, dim.x / 2, dim.y)
            vAxis.stroke().set(SvgColors.RED)
            val c = SvgCircleElement(dim.x / 2, dim.y / 2, 2.0)
            c.stroke().set(SvgColors.RED)

            val g = SvgGElement()
            g.children().add(hAxis)
            g.children().add(vAxis)
            g.children().add(c)
            return g
        }

        private fun createTextLabel(
            hAnchor: Text.HorizontalAnchor,
            vAnchor: Text.VerticalAnchor,
            angle: Double
        ): TextLabel {
            val text = "Anchor: " + hAnchor.name + "-" + vAnchor.name + " angle: " + angle + "°"
            val label = TextLabel(text)
            label.addClassName(LABEL_CLASS_NAME)
            label.setHorizontalAnchor(hAnchor)
            label.setVerticalAnchor(vAnchor)
            label.rotate(angle)
            label.textColor().set(Color.DARK_BLUE)
            return label
        }
    }
}
