/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svg

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import demo.svgModel.ClipPathSvgModel
import demo.svgModel.OpacityDemoModel
import demo.svgModel.SvgImageElementModel
import org.jetbrains.letsPlot.compose.SimpleSvgPanel

fun main() = application {
    val items = listOf(
//        "Reference SVG" to ReferenceSvgModel::createModel,
        "SvgImageElement" to SvgImageElementModel::createModel,
        "clip-path" to ClipPathSvgModel::createModel,
        "Opacity Demo" to OpacityDemoModel::createModel,
    )

    val selectedIndex = remember { mutableStateOf(0) }

    Window(onCloseRequest = ::exitApplication, title = "SVG demo (Compose Desktop)") {
        MaterialTheme {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                Row(modifier = Modifier.align(Alignment.CenterHorizontally), Arrangement.spacedBy(10.dp)) {
                    Text(items[selectedIndex.value].first, Modifier.align(Alignment.CenterVertically))
                    Button(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        onClick = {
                            selectedIndex.value++
                            if (selectedIndex.value == items.size) {
                                selectedIndex.value = 0
                            }
                        }) {
                        Text("Next")
                    }
                }

                val svgRoot = items[selectedIndex.value].second()
                val width = svgRoot.width().get()?.dp ?: 800.dp
                val height = svgRoot.height().get()?.dp ?: 600.dp

                Box(
                    modifier = Modifier.fillMaxSize().padding(end = 12.dp, bottom = 12.dp)
                ) {
                    val verticalScrollState = rememberScrollState()
                    val horizontalScrollState = rememberScrollState()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(verticalScrollState)
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        SimpleSvgPanel(
                            svg = svgRoot,
                            modifier = Modifier.size(width, height)
                        )
                    }

                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(verticalScrollState)
                    )

                    HorizontalScrollbar(
                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                        adapter = rememberScrollbarAdapter(horizontalScrollState)
                    )
                }
            }
        }
    }
}
