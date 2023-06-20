package demo.plot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import plotSpec.DensitySpec
import plotSpec.PlotGridSpec

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val densityPlot = DensitySpec().createFigure()
            val plotGrid = PlotGridSpec().createFigure()

            // ToDo: save/restore state
            val preserveAspectRatio = remember { mutableStateOf(true) }

            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                ) {
                    AspectRatioRadioGroup(preserveAspectRatio)

                    PlotPanel(
                        figure = plotGrid,
                        preserveAspectRatio = preserveAspectRatio.value,
                        modifier = Modifier.fillMaxSize()
                    ) { computationMessages ->
                        computationMessages.forEach { println("[DEMO APP MESSAGE] $it") }
                    }
                }
            }
        }
    }
}