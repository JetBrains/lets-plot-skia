package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import plotSpec.ViolinSpec

fun main() {
    with(ViolinSpec()) {
        PlotSpecsDemoWindow(
            "Violin",
            createFigureList(),
        ).open()
    }
}
