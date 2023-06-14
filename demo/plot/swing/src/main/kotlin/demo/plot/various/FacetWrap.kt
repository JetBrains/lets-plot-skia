package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import plotSpec.FacetWrapSpec

fun main() {
    with(FacetWrapSpec()) {
        PlotSpecsDemoWindow(
            "Facet Wrap",
            createFigureList(),
        ).open()
    }
}
