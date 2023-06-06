package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.PlotGridSpec

fun main() {
    with(PlotGridSpec()) {
        PlotSpecsDemoWindow(
            "Plot grid",
            createFigureList().map { it.toSpec() },
        ).open()
    }
}
