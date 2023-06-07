package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.TooltipAnchorSpec

fun main() {
    with(TooltipAnchorSpec()) {
        PlotSpecsDemoWindow(
            "Tooltip Anchor",
            createFigureList().map { it.toSpec() },
        ).open()
    }
}
