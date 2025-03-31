/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package plotSpec

import demoData.AutoMpg
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.scaleColorManual
import org.jetbrains.letsPlot.themes.elementMarkdown
import org.jetbrains.letsPlot.themes.elementText
import org.jetbrains.letsPlot.themes.theme

class MarkdownSpec : PlotDemoSpec {
    override fun createFigureList(): List<Figure> {
        return listOf(
            mpg()
        )
    }

    fun mpg(): Figure {
        return letsPlot(AutoMpg.map()) +
                geomPoint(size=8) { x="engine displacement (cu. inches)"; y="miles per gallon"; color="number of cylinders" }  +
                scaleColorManual(listOf("#66c2a5", "#fc8d62", "#8da0cb"), guide="none")  +

                // Enable Markdown in all titles
                theme(title=elementMarkdown()) +

                // Adjust style of title and subtitle
                theme(plotTitle=elementText(size=30, family="Georgia", hjust=0.5),
                    plotSubtitle=elementText(family="Georgia", hjust=0.5)) +

                labs(

                    // Span styling, mixing style and emphasis
                    title=
                        """<span style="color:#66c2a5">**4**</span>, """ +
                                """<span style="color:#8da0cb">**6**</span> and """ +
                                """<span style="color:#fc8d62">**8**</span> cylinders""",

                    // Simple emphasis
                    subtitle="**City milage** *vs* **displacement**",

                    // multiline caption, multiline style span, links
                    caption="<span style='color:grey'>" +
                            "Powered by <a href='https://lets-plot.org'>Lets-Plot</a>.  \n" +
                            "Visit the <a href='https://github.com/jetbrains/lets-plot/issues'>issue tracker</a> for feedback." +
                            "</span>",

                    // Axis titles
                    x="Displacement (***inches***)",
                    y="Miles per gallon (***cty***)"
                )

    }
}
