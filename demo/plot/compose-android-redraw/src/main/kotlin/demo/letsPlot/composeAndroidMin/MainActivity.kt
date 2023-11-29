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
import ch.qos.logback.classic.android.BasicLogcatConfigurator
import kotlinx.coroutines.delay
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.xlim
import org.jetbrains.letsPlot.scale.ylim
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val xs = rememberSaveable { mutableListOf<Double>() }
            val ys = rememberSaveable { mutableListOf<Double>() }
            val maxPointsCount = 100
            val step = 0.1

            val refreshDelayMs by remember { mutableLongStateOf(32) }
            var isPaused by rememberSaveable { mutableStateOf(true) }
            var fixedAxis by rememberSaveable { mutableStateOf(true) }

            fun plot(): Plot {
                var p = letsPlot() + ylim(listOf(-1.0, 1.0)) + geomPoint() // missing geomBlank() to not fail with "no layers in plot" error on init/clean
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
                    } else {
                        // Without fixed axis remove first point when we reach maxPointsCount to keep the plot moving
                        // yet not growing indefinitely.
                        if (xs.size > maxPointsCount) {
                            xs.removeFirst()
                            ys.removeFirst()
                        }
                    }

                    val lastX = xs.lastOrNull() ?: 0.0
                    val nextX = lastX + step
                    val nextY = sin(nextX)

                    xs.add(nextX)
                    ys.add(nextY)

                    val data = mapOf<String, Any>(
                        "x" to xs,
                        "y" to ys
                    )
                    figure = plot() + geomLine(data = data, color = "black", size = 1.2) { x = "x"; y = "y" }
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
                                    ys.clear()
                                    figure = plot()
                                }, modifier = Modifier
                                    .width(100.dp)
                            ) {
                                Text(text = "Reset")
                            }
                        }
                        Row {
                            Text(
                                text = "Fixed axis:",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )
                            Checkbox(fixedAxis, onCheckedChange = { fixedAxis = it })
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

    private companion object {
        init {
            BasicLogcatConfigurator.configureDefaultContext()
        }
    }
}
