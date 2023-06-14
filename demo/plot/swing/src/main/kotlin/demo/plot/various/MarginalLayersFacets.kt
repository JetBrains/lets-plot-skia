package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import plotSpec.MarginalLayersFacetsSpec

fun main() {
    with(MarginalLayersFacetsSpec()) {
        PlotSpecsDemoWindow(
            "Marginal Layers",
            createFigureList(),
        ).open()
    }
}
