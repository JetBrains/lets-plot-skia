/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package demo.plot.median

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import demo.plot.median.ui.DemoList
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import plotSpec.DensitySpec
import plotSpec.PerfSpec
import plotSpec.PlotGridSpec

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Density Plot (Compose Desktop)") {

        val figures = listOf(
            "Density Plot" to DensitySpec().createFigure(),
            "Plot Grid" to PlotGridSpec().createFigure(),
            "25k Points" to PerfSpec().createFigure()
        )

        val preserveAspectRatio = remember { mutableStateOf(false) }
        val dummy = remember { mutableStateOf(false) }
        val figureIndex = remember { mutableStateOf(0) }

        MaterialTheme {
            Row {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(CenterVertically)
                        .width(IntrinsicSize.Max)
                ) {
                    Text("Demos:", fontWeight = FontWeight.Bold)
                    DemoList(
                        options = figures.unzip().first,
                        selectedIndex = figureIndex,
                    )
                    Row {
                        Text(
                            text = "Keep ratio:",
                            modifier = Modifier
                                .align(CenterVertically)
                        )
                        Checkbox(preserveAspectRatio.value, onCheckedChange = { preserveAspectRatio.value = it })
                    }
                    Row {
                        Text(
                            text = "Recomposition trigger",
                            modifier = Modifier
                                .align(CenterVertically)
                        )
                        Checkbox(dummy.value, onCheckedChange = { dummy.value = it })
                    }
                }
                Column(
                    modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                    ) {
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
}

