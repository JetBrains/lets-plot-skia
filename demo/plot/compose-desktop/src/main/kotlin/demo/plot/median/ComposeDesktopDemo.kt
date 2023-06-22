package demo.plot.median

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import plotSpec.DensitySpec
import plotSpec.PlotGridSpec

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Density Plot (Compose Desktop)") {

        val figures = listOf(
            "Density" to DensitySpec().createFigure(),
            "gggrid" to PlotGridSpec().createFigure()
        )

        val preserveAspectRatio = remember { mutableStateOf(false) }
        val figureIndex = remember { mutableStateOf(0) }

        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                ) {
                    @OptIn(ExperimentalLayoutApi::class)
                    (FlowRow(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        DemoRadioGroup(
                            preserveAspectRatio,
                        )

                        // ToDo: ExposedDropdownMenuBox is not available.
                        DemoDropdownMenu(
                            options = figures.unzip().first,
                            selectedIndex = figureIndex
                        )
                    })

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

