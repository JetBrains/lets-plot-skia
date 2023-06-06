package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.MarginalLayersFacetsSpec

fun main() {
    with(MarginalLayersFacetsSpec()) {
        PlotSpecsDemoWindow(
            "Marginal Layers",
            createFigureList().map { it.toSpec() },
        ).open()
    }
}
