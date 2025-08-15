/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.letsPlot.composeAndroidMin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.letsPlot.geom.geomBlank
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.xlim
import org.jetbrains.letsPlot.scale.ylim
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import kotlin.math.sin

class ComposeRedrawMainActivity : ComponentActivity() {
    private val maxPointsCount = 100
    private val step = 0.1
    private val refreshDelayMs = 32L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val xs = rememberSaveable { mutableListOf<Double>() }
            val ys = rememberSaveable { mutableListOf<Double>() }

            var isPaused by rememberSaveable { mutableStateOf(true) }
            var fixedAxis by rememberSaveable { mutableStateOf(true) }

            fun plot(): Plot {
                val data = mapOf<String, Any>(
                    "x" to xs,
                    "y" to ys
                )

                var p = letsPlot() + ylim(listOf(-1.0, 1.0))
                if (xs.isEmpty()) {
                    p += geomBlank()
                } else {
                    p += geomLine(data = data, color = "black", size = 1.2) { x = "x"; y = "y" }
                }

                if (fixedAxis) {
                    p += xlim(listOf(0.0, step * maxPointsCount))
                }

                return p
            }

            var figure: Plot by remember { mutableStateOf(plot()) }

            LaunchedEffect(key1 = isPaused) {
                while (!isPaused) {
                    delay(refreshDelayMs)

                    if (fixedAxis) {
                        // With fixed wait until we run out of axis and then clear the data to start from the beginning.
                        val firstX = xs.firstOrNull() ?: 0.0

                        if (firstX > step * maxPointsCount) { // run out of axis
                            xs.clear()
                            ys.clear()
                        }
                    }

                    if (xs.size > maxPointsCount) {
                        xs.removeAt(0)
                        ys.removeAt(0)
                    }

                    val lastX = xs.lastOrNull() ?: 0.0
                    val nextX = lastX + step
                    val nextY = sin(nextX)

                    xs.add(nextX)
                    ys.add(nextY)

                    figure = plot()
                }
            }

            MaterialTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            onClick = { isPaused = !isPaused },
                            modifier = Modifier
                                .width(100.dp)
                        ) {
                            Text(text = if (isPaused) "Run" else "Pause")
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Button(
                            onClick = {
                                xs.clear()
                                ys.clear()
                                figure = plot()
                            }, modifier = Modifier
                                .width(100.dp)
                        ) {
                            Text(text = "Reset")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Fixed axis:"
                        )
                        Checkbox(fixedAxis, onCheckedChange = { fixedAxis = it })
                    }

                    PlotPanel(
                        figure = figure,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) { computationMessages ->
                        computationMessages.forEach { println("[DEMO APP MESSAGE] $it") }
                    }
                }
            }
        }
    }
}
