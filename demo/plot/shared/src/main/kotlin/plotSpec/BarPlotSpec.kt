/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package plotSpec

import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.asDiscrete
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.scaleFillDiscrete
import org.jetbrains.letsPlot.scale.scaleFillHue

class BarPlotSpec : PlotDemoSpec {

    override fun createFigureList(): List<Figure> {
        val basic = letsPlot(DATA) +
                geomBar(alpha = 0.5) {
                    x = "time"
                    color = "time"
                    fill = "time"
                }

        // ToDo: this doesn't work the same way as in the same demo in LP.
        val fancy = letsPlot(DATA) +
                geomBar {
                    x = "time"
                    fill = asDiscrete("..count..")
                } + scaleFillHue()

        return listOf(
            basic,
            fancy,
            fancyWithWidth(0.5),
            fancyWithWidth(5.0),
        )
    }

    private fun fancyWithWidth(w: Double): Figure {
        return letsPlot(DATA) +
                geomBar(width = w) {
                    x = "time"
                    fill = "..count.."
                } + scaleFillDiscrete()
    }

    companion object {
        val DATA = mapOf(
            "time" to listOf("Lunch", "Lunch", "Dinner", "Dinner", "Dinner")
        )
    }
}