package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.RasterSpec

fun main() {
    with(RasterSpec()) {
        PlotSpecsDemoWindow(
            "Raster",
            createFigureList().map { it.toSpec() },
        ).open()
    }
}
