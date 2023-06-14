package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import plotSpec.PlotGridSpec

fun main() {
    with(PlotGridSpec()) {
        PlotSpecsDemoWindow(
            "Plot grid",
            createFigureList(),
        ).open()
    }
}
