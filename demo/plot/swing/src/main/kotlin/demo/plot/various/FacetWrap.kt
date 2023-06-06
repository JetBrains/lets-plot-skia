package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.FacetWrapSpec

fun main() {
    with(FacetWrapSpec()) {
        PlotSpecsDemoWindow(
            "Facet Wrap",
            createFigureList().map { it.toSpec() },
        ).open()
    }
}
