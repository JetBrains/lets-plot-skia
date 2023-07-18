package plotSpec

import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.sampling.samplingNone
import kotlin.random.Random

class PerfSpec : PlotDemoSpec {

        private fun points(): Plot {
            val rand = Random(12)
            val n = 25_000

            val data = mapOf(
                "x" to List(n) { rand.nextDouble() },
                "y" to List(n) { rand.nextDouble() },
                "col" to List(n) { rand.nextDouble() },
            )

            return letsPlot(data) + geomPoint(size = 8, alpha = 0.3, sampling = samplingNone) {
                x = "x"
                y = "y"
                color = "col"
            }
        }

    override fun createFigureList(): List<Figure> {
        return listOf(points())
    }
}