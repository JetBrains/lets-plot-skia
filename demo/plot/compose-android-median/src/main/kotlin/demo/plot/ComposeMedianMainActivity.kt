/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import demo.plot.ui.DemoDropdownMenu
import demo.plot.ui.DemoRadioGroup
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import org.jetbrains.letsPlot.skia.compose.PlotPanelRaw
import plotSpec.*

class ComposeMedianMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val figures = listOf(
                "Density" to DensitySpec().createFigure(),
                "gggrid" to PlotGridSpec().createFigure(),
                "Raster" to RasterSpec().createFigure(),
                "Bar" to BarPlotSpec().createFigure(),
                "Violin" to ViolinSpec().createFigure(),
                "Markdown" to MarkdownSpec().mpg(),
                "BackendError" to IllegalArgumentSpec().createFigure(),
                "FrontendError" to FrontendExceptionSpec().createRawSpec(),
            )

            val preserveAspectRatio = rememberSaveable { mutableStateOf(true) }
            val figureIndex = rememberSaveable { mutableStateOf(3) }

            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                ) {
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        DemoRadioGroup(
                            preserveAspectRatio,
                        )
                        DemoDropdownMenu(
                            options = figures.unzip().first,
                            selectedIndex = figureIndex
                        )
                    }

                    val fig = figures[figureIndex.value].second
                    if (fig is Map<*, *>) {
                        PlotPanelRaw(
                            rawSpec = fig as MutableMap<String, Any>,
                            preserveAspectRatio = preserveAspectRatio.value,
                            modifier = Modifier.fillMaxSize(),
                            errorModifier = Modifier.padding(16.dp),
                            errorTextStyle = TextStyle(color = Color(0xFF700000)),
                            computationMessagesHandler = { messages ->
                                messages.forEach { println("[DEMO APP MESSAGE] $it") }
                            }
                        )
                    } else if (fig is Figure) {
                        PlotPanel(
                            figure = fig,
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