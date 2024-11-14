/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgModel

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement

object NestedSvgModel {

    fun m(): SvgSvgElement {
        val svg = svgDocument(width = 290, height = 190) {
            rect(x=20, y=20, height=150, width=250, stroke= SvgColors.LIGHT_BLUE, fill=null)
            g(transform= translate(20, 20)) {
                g {
                    g { /* path */ }
                    g {
                        svg(165, 56) {
                            svg(0, 0)
                            svg(155, 46) {
                                g {
                                  text("some label:", 0, 12)
                                }
                                g {
                                    text("value", 128, 12) {
                                        textAnchor().set("end")
                                    }
                                }
                                g {
                                    text("only value", 64, 33)
                                }
                            }
                        }
                    }
                }
            }
        }

        return svg
    }

    fun createModel(): SvgSvgElement {
        return SvgSvgElement().apply {
            width().set(600.0)
            height().set(600.0)

            children().addAll(listOf(
                SvgSvgElement(0.0, 0.0),
                SvgSvgElement(155.072265625, 46.265625).apply {
                    x().set(27.0)
                    y().set(10.0)


                    children().addAll(listOf(
                        SvgRectElement(0.0, 0.0, 250.0, 250.0).apply {
                            fillColor().set(Color.LIGHT_GRAY)
                            opacity().set(0.5)
                        },
                        SvgTextElement(50.0, 50.0, "Nested SVG").apply {
                            textAnchor().set("middle")
                        }
                    )
                    )
                }
            )
            )
        }
    }
}