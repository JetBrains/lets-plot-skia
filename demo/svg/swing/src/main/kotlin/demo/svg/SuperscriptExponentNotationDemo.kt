/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svg

import demo.svg.utils.DemoBase
import demo.svg.utils.DemoWindow
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.Label
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.datamodel.svg.dom.*

fun main() {
    with(SuperscriptExponentNotationDemo()) {
        DemoWindow("Power exponent format",
            createSvgRoots(listOf(createModel())),
        ).open()
    }
}

private class SuperscriptExponentNotationDemo: DemoBase(DoubleVector(800.0, 1200.0)) {
    override val cssStyle: String
        get() = """
            .LBL_1 { 
                font-style:normal;
                font-weight:normal;
                font-size:18.0px;
                font-family:sans; 
            }
            .LBL_2 { 
                font-style:normal;
                font-weight:normal;
                font-size:18.0px;
                font-family:sans; 
            }
            .LBL_3 { 
                font-style:normal;
                font-weight:normal;
                font-size:18.0px;
                font-family:sans; 
            }
            .LBL_4 { 
                font-style:normal;
                font-weight:normal;
                font-size:18.0px;
                font-family:sans; 
            }
            .LBL_5 { 
                font-style:normal;
                font-weight:normal;
                font-size:18.0px;
                font-family:sans; 
            }
            .LBL_6 { 
                font-style:normal;
                font-weight:normal;
                font-size:18.0px;
                font-family:sans; 
            }
            .LBL_7 { 
                font-style:normal;
                font-weight:normal;
                font-size:18.0px;
                font-family:serif; 
            }
            .LBL_8 { 
                font-style:normal;
                font-weight:normal;
                font-size:18.0px;
                font-family:Courier; 
            }
            .LBL_9 { 
                font-style:normal;
                font-weight:normal;
                font-size:10.0px;
                font-family:sans; 
            }
            .LBL_10 { 
                font-style:normal;
                font-weight:normal;
                font-size:24.0px;
                font-family:sans; 
            }
            .LBL_11 { 
                font-style:italic;
                font-weight:normal;
                font-size:18.0px;
                font-family:sans; 
            }
            .LBL_12 { 
                font-style:normal;
                font-weight:bold;
                font-size:18.0px;
                font-family:sans; 
            }
            """.trimIndent()

    fun createItalicModel(): GroupComponent {
        val groupComponent = GroupComponent()
        val label = Label("""Power text: -1.5·\(10^{-15}\)""")
        SvgUtils.transformTranslate(label.rootGroup, 300.0, 300.0)

        groupComponent.add(createLabelExample(12, DoubleVector.ZERO))
        return groupComponent

    }

    fun createSimpleModel(): GroupComponent {
        val groupComponent = GroupComponent()
        val label = Label("""Power text: -1.5·\(10^{-15}\)""")
        SvgUtils.transformTranslate(label.rootGroup, 300.0, 300.0)

        groupComponent.add(label)
        return groupComponent

    }

    fun createModel(): GroupComponent {
        val groupComponent = GroupComponent()

        val exampleIdIter = generateSequence(1) { it + 1 }.iterator()
        val shiftIter = generateSequence(INIT_SHIFT) { DoubleVector(it.x, it.y + DY_SHIFT) }.iterator()

        // Example #1
        groupComponent.add(createLabelExample(
            exampleIdIter.next(),
            shiftIter.next(),
        ))
        // Example #2
        groupComponent.add(createLabelExample(
            exampleIdIter.next(),
            shiftIter.next(),
            hAnchor = Text.HorizontalAnchor.MIDDLE,
            vAnchor = Text.VerticalAnchor.CENTER,
        ))
        // Example #3
        groupComponent.add(createLabelExample(
            exampleIdIter.next(),
            shiftIter.next(),
            hAnchor = Text.HorizontalAnchor.RIGHT,
            vAnchor = Text.VerticalAnchor.TOP,
        ))
        // Example #4
        groupComponent.add(createLabelExample(
            exampleIdIter.next(),
            shiftIter.next(),
            hAnchor = Text.HorizontalAnchor.MIDDLE,
            vAnchor = Text.VerticalAnchor.CENTER,
            angle = 45.0,
        ))
        // Example #5
        groupComponent.add(createLabelExample(
            exampleIdIter.next(),
            shiftIter.next(),
            hAnchor = Text.HorizontalAnchor.MIDDLE,
            vAnchor = Text.VerticalAnchor.CENTER,
            angle = 90.0,
        ))
        // Example #6
        groupComponent.add(createLabelExample(
            exampleIdIter.next(),
            shiftIter.next(),
            hAnchor = Text.HorizontalAnchor.MIDDLE,
            vAnchor = Text.VerticalAnchor.CENTER,
            angle = 180.0,
        ))
        // Example #7
        groupComponent.add(createLabelExample(exampleIdIter.next(), shiftIter.next()))
        // Example #8
        groupComponent.add(createLabelExample(exampleIdIter.next(), shiftIter.next()))
        // Example #9
        groupComponent.add(createLabelExample(exampleIdIter.next(), shiftIter.next()))
        // Example #10
        groupComponent.add(createLabelExample(
            exampleIdIter.next(),
            shiftIter.next(),
            hAnchor = Text.HorizontalAnchor.RIGHT,
            vAnchor = Text.VerticalAnchor.TOP
        ))
        // Example #11
        groupComponent.add(createLabelExample(exampleIdIter.next(), shiftIter.next()))
        // Example #12
        groupComponent.add(createLabelExample(exampleIdIter.next(), shiftIter.next()))

        return groupComponent
    }


    companion object {
        private val INIT_SHIFT = DoubleVector(300.0, 0.0)
        private const val DY_SHIFT = 100.0
        private val DIM = DoubleVector(200.0, 50.0)
        private const val FORMULA = """-1.5·\(10^{-15}\)"""

        private fun createLabelExample(
            exampleId: Int,
            shift: DoubleVector,
            hAnchor: Text.HorizontalAnchor = Text.HorizontalAnchor.LEFT,
            vAnchor: Text.VerticalAnchor = Text.VerticalAnchor.BOTTOM,
            angle: Double = 0.0,
        ): SvgGElement {
            val textLabel = createTextLabel(exampleId, hAnchor, vAnchor, angle).also {
                it.moveTo(DIM.x / 2, DIM.y / 2)
            }
            val exampleSvgGElement = SvgGElement()
            exampleSvgGElement.children().add(createAxis())
            exampleSvgGElement.children().add(textLabel.rootGroup)
            SvgUtils.transformTranslate(exampleSvgGElement, shift.x, shift.y)
            return exampleSvgGElement
        }

        private fun createAxis(): SvgElement {
            val hAxis = SvgLineElement(0.0, DIM.y / 2, DIM.x, DIM.y / 2).also {
                it.stroke().set(SvgColors.RED)
            }
            val vAxis = SvgLineElement(DIM.x / 2, 0.0, DIM.x / 2, DIM.y).also {
                it.stroke().set(SvgColors.RED)
            }
            val origin = SvgCircleElement(DIM.x / 2, DIM.y / 2, 2.0).also {
                it.fill().set(SvgColors.WHITE)
                it.stroke().set(SvgColors.RED)
            }

            val g = SvgGElement()
            g.children().add(hAxis)
            g.children().add(vAxis)
            g.children().add(origin)
            return g
        }

        private fun createTextLabel(
            exampleId: Int,
            hAnchor: Text.HorizontalAnchor,
            vAnchor: Text.VerticalAnchor,
            angle: Double,
        ): Label {
            val label = Label("$FORMULA ($exampleId)")
            label.addClassName("LBL_$exampleId")
            label.setHorizontalAnchor(hAnchor)
            label.setVerticalAnchor(vAnchor)
            label.rotate(angle)
            return label
        }
    }
}
