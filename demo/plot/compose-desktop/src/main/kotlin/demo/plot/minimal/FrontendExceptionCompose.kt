/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.minimal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.letsPlot.skia.compose.PlotPanelRaw
import plotSpec.FrontendExceptionSpec

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Frontend exception Test (Compose Desktop)") {
        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
            ) {

                PlotPanelRaw(
                    rawSpec = FrontendExceptionSpec().createRawSpec(),
                    modifier = Modifier.fillMaxSize(),
                    errorModifier = Modifier.padding(16.dp),
                    errorTextStyle = TextStyle(color = Color(0xFF700000)),
                    preserveAspectRatio = false,
                ) { computationMessages ->
                    computationMessages.forEach { println("[DEMO APP MESSAGE] $it") }
                }
            }
        }
    }
}

