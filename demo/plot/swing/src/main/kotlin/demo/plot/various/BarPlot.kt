package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.BarPlotSpec

fun main() {
    with(BarPlotSpec()) {
        PlotSpecsDemoWindow(
            "Bar-plot",
            createFigureList().map { it.toSpec() },
        ).open()
    }
}
