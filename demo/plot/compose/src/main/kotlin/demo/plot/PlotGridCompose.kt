package demo.plot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.letsPlot.compose.ui.PlotPanel
import plotSpec.PlotGridSpec

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Plot Grid (Compose Desktop)") {
        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
            ) {

                PlotPanel(
                    figure = PlotGridSpec().createFigure(),
                    modifier = Modifier.fillMaxSize()
                ) { computationMessages ->
                    computationMessages.forEach { println("[DEMO APP MESSAGE] $it") }
                }
            }
        }
    }
}