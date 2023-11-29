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
            val step = 0.1
            val pointsLimit by rememberSaveable { mutableIntStateOf(100) }
            val xs = remember { mutableListOf<Double>() }
            val ys = remember { mutableListOf<Double>() }
            val updatePause by remember { mutableLongStateOf(32L) }
            var isPaused by rememberSaveable { mutableStateOf(true) }
            var fixedAxis by rememberSaveable { mutableStateOf(true) }

            fun blankPlot(): Plot {
                var p = letsPlot() + ylim(listOf(-1.0, 1.0)) + geomPoint() // missing geomBlank() to not fail with "no layers in plot" error on init/clean
                if (fixedAxis) {
                    p += xlim(listOf(0.0, step * pointsLimit))
                }
                return p
            }

            var figure: Plot by remember { mutableStateOf(blankPlot()) }

            LaunchedEffect(key1 = isPaused) {
                while (!isPaused) {
                    delay(updatePause)

                    if (xs.size > pointsLimit) {
                        xs.removeFirst()
                        ys.removeFirst()
                    }

                    val firstX = xs.firstOrNull() ?: 0.0
                    if (fixedAxis && firstX > step * pointsLimit) { // run out of axis
                        xs.clear()
                        ys.clear()
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
                    figure = blankPlot() + geomLine(data = data, color = "black", size = 1.2) { x = "x"; y = "y" }
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
                                    figure = blankPlot()
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
