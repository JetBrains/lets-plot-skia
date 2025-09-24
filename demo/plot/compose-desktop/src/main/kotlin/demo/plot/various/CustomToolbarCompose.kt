/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.various

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.letsPlot.compose.PlotPanelRaw
import org.jetbrains.letsPlot.compose.PlotTool
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.AutoSpec


@OptIn(ExperimentalLayoutApi::class)
fun main() = application {
    MaterialTheme {    var activeTool by remember { mutableStateOf<PlotTool?>(PlotTool.PAN) }

    Window(onCloseRequest = ::exitApplication, title = "ggtb() (Compose Desktop)") {
            FlowRow(
                modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
            ) {
                AutoSpec().createFigureList().forEach { figure ->
                    Column {
                        Row {
                            Button(onClick = { activeTool = PlotTool.PAN }) { Text("Pan") }
                            Button(onClick = { activeTool = PlotTool.BBOX_ZOOM }) { Text("Zoom") }
                            Button(onClick = { activeTool = null }) { Text("Disable interactivity") }
                        }
                        PlotPanelRaw(
                            rawSpec = figure.toSpec(),
                            interactiveTool = activeTool,
                            preserveAspectRatio = false,
                            modifier = Modifier.fillMaxSize()
                        ) { computationMessages ->
                            computationMessages.forEach { println("[DEMO APP MESSAGE] $it") }
                        }
                    }
                }
            }
        }
    }
}

