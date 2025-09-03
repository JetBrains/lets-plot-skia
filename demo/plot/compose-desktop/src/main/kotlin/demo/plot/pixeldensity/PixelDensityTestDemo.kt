/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.pixeldensity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import org.jetbrains.letsPlot.themes.flavorDarcula
import org.jetbrains.letsPlot.themes.flavorHighContrastDark

fun main() = application {
    var pixelDensity by remember { mutableFloatStateOf(1.0f) }

    Window(onCloseRequest = ::exitApplication, title = "Pixel Density Test Demo") {
        MaterialTheme {
            CompositionLocalProvider(LocalDensity provides Density(pixelDensity)) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Pixel density controls
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Current Pixel Density: ${String.format("%.2f", pixelDensity)}")
                            Text("Effective DPI: ${String.format("%.0f", pixelDensity * 96)}")

                            Slider(
                                value = pixelDensity,
                                onValueChange = { pixelDensity = it },
                                valueRange = 0.5f..3.0f,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(onClick = { pixelDensity = 1.0f }) { Text("Normal (1.0x)") }
                                Button(onClick = { pixelDensity = 1.5f }) { Text("HiDPI (1.5x)") }
                                Button(onClick = { pixelDensity = 2.0f }) { Text("Retina (2.0x)") }
                                Button(onClick = { pixelDensity = 0.75f }) { Text("Low DPI (0.75x)") }
                            }
                        }
                    }

                    // Plot panel that reacts to pixel density changes
                    PlotPanel(
                        figure = createTestFigure(),
                        preserveAspectRatio = true,
                        modifier = Modifier.fillMaxSize(),
                        computationMessagesHandler = { messages ->
                            messages.forEach { println("Plot: $it") }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Card(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        content()
    }
}

private fun createTestFigure(): Figure {
    val n = 100
    val rand = kotlin.random.Random(42)

    val data = mapOf(
        "x" to List(n) { rand.nextDouble() * 10 },
        "y" to List(n) { rand.nextDouble() * 10 },
        "color" to List(n) { listOf("A", "B", "C", "D").random(rand) }
    )

    return letsPlot(data) +
            geomPoint(size = 3.0, alpha = 0.7) {
                x = "x"; y = "y"; color = "color"
            } + flavorHighContrastDark()
}