/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.qos.logback.classic.android.BasicLogcatConfigurator
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import plotSpec.DensitySpec

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val densityPlot = DensitySpec().createFigure()
            val dummy = remember { mutableStateOf(false) }
            val keepRatio = remember { mutableStateOf(false) }
            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                ) {
                    Row {
                        Text(
                            text = "Keep ratio:",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                        Checkbox(keepRatio.value, onCheckedChange = { keepRatio.value = it })
                    }
                    Row {
                        Text(
                            text = "Recomposition trigger",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                        Checkbox(dummy.value, onCheckedChange = { dummy.value = it })
                    }
                    PlotPanel(
                        figure = densityPlot,
                        preserveAspectRatio = keepRatio.value,
                        modifier = Modifier.fillMaxSize()
                    ) { computationMessages ->
                        computationMessages.forEach { println("[DEMO APP MESSAGE] $it") }
                    }

//                    SandboxPanel(
//                        color = Color.Red,
//                        modifier = Modifier.fillMaxSize()
//                    )
                }
            }
        }
    }

    private companion object {
        init {
            BasicLogcatConfigurator.configureDefaultContext()
        }
    }
}