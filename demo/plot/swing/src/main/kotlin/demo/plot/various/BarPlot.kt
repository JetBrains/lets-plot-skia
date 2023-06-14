package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import plotSpec.BarPlotSpec

fun main() {
    with(BarPlotSpec()) {
        PlotSpecsDemoWindow(
            "Bar-plot",
            createFigureList(),
        ).open()
    }
}
