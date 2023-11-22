/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package demo.plot.redraw

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import org.jetbrains.letsPlot.geom.geomDensity
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.xlim
import org.jetbrains.letsPlot.scale.ylim
import org.jetbrains.letsPlot.skia.compose.PlotPanel


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Fast Plot Redraw (Compose Desktop)") {
        var rand by remember { mutableStateOf(java.util.Random()) }
        val maxN by remember { mutableStateOf(600) }
        val xs by remember { mutableStateOf(mutableListOf<Double>()) }
        val updatePause by remember { mutableStateOf(32L) }
        var isPaused by remember { mutableStateOf(true) }
        var limAxis by remember { mutableStateOf(true) }
        var figure: Plot by remember { mutableStateOf(plotBase(limAxis)) }

        LaunchedEffect(key1 = isPaused) {
            while (!isPaused) {
                delay(updatePause)

                xs.add(rand.nextGaussian())
                if (xs.size > maxN) {
                    xs.removeFirst()
                }

                val data = mapOf<String, Any>("x" to xs)
                figure = plotBase(limAxis) + geomDensity(data = data, color = "black", size = 1.2) { x = "x" }
            }
        }

        MaterialTheme {
            Row {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .width(IntrinsicSize.Max)
                        .padding(10.dp)
                ) {
                    Row {
                        Button(
                            onClick = { isPaused = !isPaused },
                            modifier = Modifier
                                .width(100.dp)
                                .padding(horizontal = 10.dp)
                        ) {
                            Text(text = if (isPaused) "Run" else "Pause")
                        }
                        Button(
                            onClick = {
                                xs.clear()
                                rand = java.util.Random()
                                figure = plotBase(limAxis)
                            }, modifier = Modifier
                                .width(100.dp)
                        ) {
                            Text(text = "Reset")
                        }
                    }
                    Row {
                        Text(
                            text = "Axis limits:",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                        Checkbox(limAxis, onCheckedChange = { limAxis = it })
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                    ) {
                        PlotPanel(
                            figure = figure,
                            modifier = Modifier
                                .fillMaxSize()
                        ) { computationMessages ->
                            computationMessages.forEach { println("[DEMO APP MESSAGE] $it") }
                        }
                    }
                }
            }
        }
    }
}

private fun plotBase(limAxis: Boolean): Plot {
    var p = letsPlot() + geomPoint()
    if (limAxis) {
        p += xlim(listOf(-3.0, 3.0)) + ylim(listOf(0.0, 0.5))
    }
    return p
}
