package demo.plot

//import org.jetbrains.letsPlot.swing.skia.createSkikoSwingComponent
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.compose.ui.PlotPanel
import plotSpec.DensitySpec

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Density Plot (Compose Desktop)") {
        val densityPlot: Figure = DensitySpec().createFigure()
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