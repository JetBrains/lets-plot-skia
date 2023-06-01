package demo.plot

import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import jetbrains.datalore.vis.svgMapper.skia.plotComponent
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.DensitySpec

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Density Plot (Compose Desktop)") {
        val rawPlotSpec = DensitySpec().createFigure().toSpec()
        MaterialTheme {
            SwingPanel(
                modifier = Modifier.size(600.dp, 400.dp),
                factory = {
                    plotComponent(rawPlotSpec)
                }
            )
        }
    }
}