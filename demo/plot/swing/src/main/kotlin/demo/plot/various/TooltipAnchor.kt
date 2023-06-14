package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import plotSpec.TooltipAnchorSpec

fun main() {
    with(TooltipAnchorSpec()) {
        PlotSpecsDemoWindow(
            "Tooltip Anchor",
            createFigureList(),
        ).open()
    }
}
