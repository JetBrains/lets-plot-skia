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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import plotSpec.DensitySpec

class ComposeMinDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var counter: Int by remember { mutableStateOf(0) }

            val densityPlot = DensitySpec().createFigure()
            val keepRatio = rememberSaveable { mutableStateOf(false) }

            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxSize().padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                ) {
                    Text(
                        text = """
                            Enable logging to see recompositions:
                            org.jetbrains.letsPlot.skia.compose.util.NaiveLoggerKt.ENABLED
                            """.trimIndent(),
                        fontSize = 10.sp,
                    )
                    Button(onClick =  { counter++ }) {
                        Text("recomposition checker (already clicked $counter times)")
                    }

                    Row {
                        Text(
                            text = "Keep ratio:",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                        Checkbox(keepRatio.value, onCheckedChange = { keepRatio.value = it })
                    }
                    PlotPanel(
                        figure = densityPlot,
                        preserveAspectRatio = keepRatio.value,
                        modifier = Modifier.fillMaxSize()
                    ) { computationMessages ->
                        computationMessages.forEach { println("[DEMO APP MESSAGE] $it") }
                    }
                }
            }
        }
    }
}