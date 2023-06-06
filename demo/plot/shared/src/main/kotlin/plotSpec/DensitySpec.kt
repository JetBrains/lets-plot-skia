package plotSpec

import demoData.AutoMpg
import demoData.Iris
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.coord.coordFixed
import org.jetbrains.letsPlot.geom.geomDensity
import org.jetbrains.letsPlot.letsPlot

class DensitySpec : PlotDemoSpec {
    override fun createFigure(): Figure {
        val rand = java.util.Random()
        val n = 200
        val xs = List(n) { rand.nextGaussian() }
        val data = mapOf<String, Any>(
            "x" to xs,
            "w" to xs.map { if (it < 0.0) 2.0 else 0.5 }
        )

        return letsPlot(data) + geomDensity(color = "black", size = 1.2) {
            x = "x"
        }
    }

    override fun createFigureList(): List<Figure> {
        val sepalLength = letsPlot(Iris.map()) +
                geomDensity(alpha = 0.7) {
                    x = "sepal length (cm)"
                    color = "sepal width (cm)"
                    fill = "target"
                }

        val sepalLengthCoordFixed = letsPlot(Iris.map()) +
                geomDensity(alpha = 0.7) {
                    x = "sepal length (cm)"
                    color = "sepal width (cm)"
                    fill = "target"
                } + coordFixed()

        val withQuantileAes = letsPlot(AutoMpg.map()) +
                geomDensity(alpha = 0.7, size = 2) {
                    x = "miles per gallon"
                    group = "number of cylinders"
                    color = "..quantile.."
                }

        val withQuantileLines = letsPlot(Iris.map()) +
                geomDensity(
                    color = "black",
                    quantiles = listOf(0, 0.02, 0.1, 0.5, 0.9, 0.98, 1),
                    quantileLines = true
                ) {
                    x = "sepal length (cm)"
                    group = "target"
                    fill = "..quantile.."
                }

        return listOf(
            sepalLength,
            sepalLengthCoordFixed,
            withQuantileAes,
            withQuantileLines
        )
    }
}