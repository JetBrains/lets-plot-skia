package demo.plot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import demo.plot.ui.DemoDropdownMenu
import demo.plot.ui.DemoRadioGroup
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import plotSpec.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val figures = listOf(
                "Density" to DensitySpec().createFigure(),
                "gggrid" to PlotGridSpec().createFigure(),
                "Raster" to RasterSpec().createFigure(),
                "Bar" to BarPlotSpec().createFigure(),
                "Violin" to ViolinSpec().createFigure(),
            )

            // ToDo: save/restore state
            val preserveAspectRatio = remember { mutableStateOf(false) }
            val figureIndex = remember { mutableStateOf(0) }

            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                ) {
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        DemoRadioGroup(
                            preserveAspectRatio,
                        )
                        DemoDropdownMenu(
                            options = figures.unzip().first,
                            selectedIndex = figureIndex
                        )
                    }

                    PlotPanel(
                        figure = figures[figureIndex.value].second,
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