package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import plotSpec.DensitySpec

fun main() {
    with(DensitySpec()) {
        PlotSpecsDemoWindow(
            "Density plot",
            createFigureList(),
        ).open()
    }
}
