package demo.plot

import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.skiko.swing.createSkikoSwingComponent
import plotSpec.PlotGridSpec

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Plot Grid (Compose Desktop)") {
        val densityPlot: Figure = PlotGridSpec().createFigure()
        MaterialTheme {
            SwingPanel(
                modifier = Modifier.size(600.dp, 400.dp),
                factory = {
                    densityPlot.createSkikoSwingComponent { computationMessages ->
                        computationMessages.forEach { println("[PLOT MESSAGE] $it") }
                    }
                }
            )
        }
    }
}