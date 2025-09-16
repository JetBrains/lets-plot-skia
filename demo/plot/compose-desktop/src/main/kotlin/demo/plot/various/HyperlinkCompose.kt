/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.various

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.letsPlot.compose.PlotPanel
import plotSpec.HyperlinkSpec

@OptIn(ExperimentalLayoutApi::class)
fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Hyperlink (Compose Desktop)") {
        MaterialTheme {
            FlowRow(
                modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
            ) {
                HyperlinkSpec().createFigureList().forEach { figure ->
                    Column {
                        PlotPanel(
                            figure = figure,
                            modifier = Modifier.size(610.dp)
                        ) { computationMessages ->
                            computationMessages.forEach { println("[DEMO APP MESSAGE] $it") }
                        }
                    }
                }
            }
        }
    }
}

