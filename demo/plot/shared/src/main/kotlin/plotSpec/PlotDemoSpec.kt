package plotSpec

import org.jetbrains.letsPlot.Figure

interface PlotDemoSpec {
    fun createFigure(): Figure {
        return createFigureList().first()
    }

    fun createFigureList(): List<Figure>
}