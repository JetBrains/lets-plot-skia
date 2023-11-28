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
import org.jetbrains.letsPlot.geom.geomDensity
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.xlim
import org.jetbrains.letsPlot.scale.ylim
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var rand by rememberSaveable { mutableStateOf(Random()) }
            val pointsLimit by rememberSaveable { mutableIntStateOf(600) }
            val values = remember { mutableListOf<Double>() }
            val updatePause by remember { mutableLongStateOf(32L) }
            var isPaused by rememberSaveable { mutableStateOf(true) }
            var limAxis by rememberSaveable { mutableStateOf(true) }

            fun blankPlot(): Plot {
                var p = letsPlot() + geomPoint() // missing geomBlank() to not fail with "no layers in plot" error on init/clean
                if (limAxis) {
                    p += xlim(listOf(-3.0, 3.0)) + ylim(listOf(0.0, 0.5))
                }
                return p
            }

            var figure: Plot by remember { mutableStateOf(blankPlot()) }

            LaunchedEffect(key1 = isPaused) {
                while (!isPaused) {
                    delay(updatePause)

                    values.add(rand.nextGaussian())
                    if (values.size > pointsLimit) {
                        values.removeFirst()
                    }

                    val data = mapOf<String, Any>("value" to values)
                    figure = blankPlot() + geomDensity(data = data, color = "black", size = 1.2) { x = "value" }
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
                                    values.clear()
                                    rand = Random()
                                    figure = blankPlot()
                                }, modifier = Modifier
                                    .width(100.dp)
                            ) {
                                Text(text = "Reset")
                            }
                        }
                        Row {
                            Text(
                                text = "Axis limits:",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )
                            Checkbox(limAxis, onCheckedChange = { limAxis = it })
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
