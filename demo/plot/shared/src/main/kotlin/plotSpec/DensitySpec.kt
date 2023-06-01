package plotSpec

import org.jetbrains.letsPlot.Figure
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
}