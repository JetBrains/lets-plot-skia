package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.DensitySpec

fun main() {
    with(DensitySpec()) {
        PlotSpecsDemoWindow(
            "Density plot",
            createFigureList().map { it.toSpec() },
        ).open()
    }
}
