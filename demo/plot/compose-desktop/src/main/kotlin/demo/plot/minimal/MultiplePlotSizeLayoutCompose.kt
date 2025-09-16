/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.minimal

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.letsPlot.compose.PlotPanel
import plotSpec.BarPlotSpec
import plotSpec.DensitySpec

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Multiple Plot Size Layout (Compose Desktop)") {
        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
            ) {

                PlotPanel(
                    figure = BarPlotSpec().createFigure(),
                    modifier = Modifier.height(100.dp).width(100.dp)
                ) { computationMessages ->
                    computationMessages.forEach { println("[DEMO APP MESSAGE] $it") }
                }

                PlotPanel(
                    figure = DensitySpec().createFigure(),
                    modifier = Modifier.height(100.dp).width(100.dp)
                ) { computationMessages ->
                    computationMessages.forEach { println("[DEMO APP MESSAGE] $it") }
                }
            }
        }
    }
}

