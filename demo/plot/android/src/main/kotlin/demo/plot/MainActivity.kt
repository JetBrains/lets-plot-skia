package demo.plot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.letsPlot.compose.ui.PlotPanel
import plotSpec.DensitySpec
import plotSpec.PlotGridSpec

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val densityPlot = DensitySpec().createFigure()
            val plotGrid = PlotGridSpec().createFigure()

            MaterialTheme {
                PlotPanel(
                    figure = densityPlot,
                    modifier = Modifier.size(600.dp, 400.dp),
                ) { computationMessages ->
                    computationMessages.forEach { println("[PLOT MESSAGE] $it") }
                }
            }
        }
    }
}