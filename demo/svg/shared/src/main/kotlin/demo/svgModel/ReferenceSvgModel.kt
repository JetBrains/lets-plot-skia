/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgModel

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Color.Companion.LIGHT_GREEN
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors.Companion.create
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

object ReferenceSvgModel {
    fun createModel(): SvgSvgElement = SvgSvgElement(500.0, 500.0).apply {
        g {
            slimG(16) {
                var i = 20.0
                while (i < 400) {
                    slimLine(i, 0.0, i, 200.0, LIGHT_GREEN, 20.0)
                    i += 40
                }

                slimCircle(300.0, 60.0, 50.0, Color.DARK_BLUE, Color.LIGHT_YELLOW, 3.0)
                slimPath(createClosedPathFrom(150.0, 175.0), Color.DARK_GREEN, Color.CYAN, 2.0)
                slimRect(160.0, 50.0, 80.0, 50.0, Color.DARK_MAGENTA, Color.LIGHT_MAGENTA, 1.0)
            }

            style(
                mapOf(
                    "TEXT1" to TextStyle(FontFamily.SERIF.name, FontFace.ITALIC, 15.0, Color.BLUE),
                    "TEXT2" to TextStyle(FontFamily.SERIF.name, FontFace.BOLD, 20.0, Color.RED),
                    "EMC2" to TextStyle(FontFamily.HELVETICA.name, FontFace.BOLD, 22.0, Color.BLUE),
                )
            )

            // Superscript with baseline-shift
            text(styleClass = "EMC2") {
                transform().set(SvgTransformBuilder().translate(300.0, 150.0).build())
                addTSpan(SvgTSpanElement("E=mc"))
                addTSpan(SvgTSpanElement("2").apply {
                    setAttribute("baseline-shift", "super")
                    setAttribute("font-size", "75%")
                })
                addTSpan(SvgTSpanElement("with baseline-shift").apply {
                    setAttribute("font-size", "50%")
                })
            }

            // Subscript with dy
            text(styleClass = "EMC2") {
                transform().set(SvgTransformBuilder().translate(300.0, 180.0).build())
                addTSpan(SvgTSpanElement("E=mc"))
                addTSpan(SvgTSpanElement("2").apply {
                    setAttribute("dy", "-0.4em")
                    setAttribute("font-size", "75%")
                })
                addTSpan(SvgTSpanElement("with dy").apply {
                    setAttribute("font-size", "50%")
                })
            }

            text("Slim elements", x = 30.0, y = 85.0, styleClass = "TEXT1") {
                transform().set(SvgTransformBuilder().rotate(-45.0, 20.0, 100.0).build())
            }

            g {
                var i = 220.0
                while (i < 400) {
                    line(0.0, i, 400.0, i, create(LIGHT_GREEN), 20.0)
                    i += 40
                }
            }

            text("Svg elements", x = 20.0, y = 225.0, styleClass = "TEXT2") {
                stroke().set(SvgColors.CORAL)
                strokeWidth().set(1.0)
            }

            circle(300.0, 260.0, 50.0, fill = SvgColors.LIGHT_PINK)
            rect(160.0, 250.0, 80.0, 50.0, SvgColors.GRAY, SvgColors.LIGHT_YELLOW) {
                getAttribute(SvgConstants.SVG_STROKE_DASHARRAY_ATTRIBUTE).set(getDashes(4.3, 4.3, 1.0))
            }

            path(SvgColors.ORANGE, SvgColors.NONE, createClosedPathFrom(150.0, 375.0), 2.0) {
                transform().set(SvgTransformBuilder().translate(0.0, -30.0).skewY(20.0).build())
            }

            path(SvgColors.ORANGE, SvgColors.NONE, createUnclosedPathFrom(0.0, 200.0), 1.5)
            path(fill = SvgColors.LIGHT_BLUE, pathData = createHoledPathFrom(350.0, 350.0))

            g {
                transform().set(
                    SvgTransformBuilder()
                        .translate(100.0, 400.0)
                        .rotate(90.0)
                        .build()
                )
                text("Nested rotated", 20.0, 25.0, styleClass = "TEXT2") {
                    stroke().set(SvgColors.CORAL)
                    strokeWidth().set(1.0)
                }
            }

        }
    }

    private fun createClosedPathFrom(x: Double, y: Double): SvgPathData {
        return SvgPathDataBuilder(false)
            .moveTo(x, y, true)
            .verticalLineTo(-100.0)
            .ellipticalArc(100.0, 100.0, 0.0, false, false, -100.0, 100.0)
            .closePath()
            .build()
    }

    private fun createUnclosedPathFrom(x: Double, y: Double): SvgPathData {
        return SvgPathDataBuilder(true)
            .moveTo(x, y)
            .interpolatePoints(createSawPointsFrom(x, y), SvgPathDataBuilder.Interpolation.LINEAR)
            .build()
    }

    private fun createHoledPathFrom(x: Double, y: Double): SvgPathData {
        return SvgPathDataBuilder(false)
            .moveTo(x, y, true)
            .horizontalLineTo(50.0)
            .verticalLineTo(50.0)
            .horizontalLineTo(-50.0)
            .closePath()
            .moveTo(x + 10, y + 10, true)
            .horizontalLineTo(30.0)
            .verticalLineTo(30.0)
            .horizontalLineTo(-30.0)
            .closePath()
            .build()
    }

    private fun createSawPointsFrom(x: Double, y: Double): List<DoubleVector> {
        val points = mutableListOf<DoubleVector>()
        points.add(DoubleVector(x, y))
        var i = 0.0
        while (i < 400) {
            points.add(DoubleVector(i + 20, y - 10))
            points.add(DoubleVector(i + 40, y))
            points.add(DoubleVector(i + 60, y + 10))
            points.add(DoubleVector(i + 80, y))
            i += 80
        }
        return points
    }

    private fun getDashes(d1: Double, d2: Double, strokeWidth: Double): String {
        val dash1 = d1 * strokeWidth
        val dash2 = d2 * strokeWidth
        return "$dash1,$dash2"
    }
}