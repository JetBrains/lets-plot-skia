/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package plotSpec

import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.themes.theme
import kotlin.math.pow

class SuperscriptExponentNotationSpec : PlotDemoSpec {
    override fun createFigureList(): List<Figure> {
        return listOf(
            simple(),
            f()

        )
    }

    private fun f(): Figure =
        letsPlot() +
                geomPoint {
                    x = listOf(1e-7, 2e-7, 3e-7)
                    y = listOf(0, 0, 0)
                } +
                theme(exponentFormat = "pow")

    private fun simple(): Figure {
        val xs: List<Double> = (-10..10).map(Int::toDouble).toList()
        val f: (Double) -> Double = { it * 10.0.pow(-5) }
        val data = mapOf("x" to xs, "y" to xs.map(f))
        return letsPlot(data) {
            x = "x"; y = "y"
        } + geomPoint() + ggtitle("No transform") + theme(exponentFormat = "pow")
    }
}
