package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import plotSpec.ThemeOptionsSpec

fun main() {
    with(ThemeOptionsSpec()) {
        PlotSpecsDemoWindow(
            "Theme options",
            createFigureList(),
        ).open()
    }
}
