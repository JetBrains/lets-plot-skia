package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.ThemeOptionsSpec

fun main() {
    with(ThemeOptionsSpec()) {
        PlotSpecsDemoWindow(
            "Theme options",
            createFigureList().map { it.toSpec() },
        ).open()
    }
}
