package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.ViolinSpec

fun main() {
    with(ViolinSpec()) {
        PlotSpecsDemoWindow(
            "Violin",
            createFigureList().map { it.toSpec() },
        ).open()
    }
}
