package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import plotSpec.RasterSpec

fun main() {
    with(RasterSpec()) {
        PlotSpecsDemoWindow(
            "Raster",
            createFigureList(),
        ).open()
    }
}
