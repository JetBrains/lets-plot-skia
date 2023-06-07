package plotSpec

import demoData.Raster.rasterData_Blue
import demoData.Raster.rasterData_RGB
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.geom.geomRaster
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.scaleFillIdentity

class RasterSpec : PlotDemoSpec {

    override fun createFigureList(): List<Figure> {
        return listOf(
            rasterPlot(rasterData_Blue(), scaleFillIdentity = false),
            rasterPlot(rasterData_RGB(), scaleFillIdentity = true)
        )
    }

    private fun rasterPlot(data: Map<*, *>, scaleFillIdentity: Boolean): Figure {
        var plot = letsPlot(data) +
                geomRaster {
                    x = "x"
                    y = "y"
                    fill = "fill"
                    alpha = "alpha"
                }

        if (scaleFillIdentity) {
            plot += scaleFillIdentity()
        }

        return plot
    }
}