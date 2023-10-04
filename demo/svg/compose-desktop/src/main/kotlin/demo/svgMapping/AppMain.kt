/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import demo.svgModel.ClipPathSvgModel
import demo.svgModel.ReferenceSvgModel
import demo.svgModel.SvgImageElementModel
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.skia.awt.view.SvgPanel

fun main() = application {
    val items = listOf(
        "Reference SVG" to ReferenceSvgModel::createModel,
        "SvgImageElement" to SvgImageElementModel::createModel,
        "clip-path" to ClipPathSvgModel::createModel
    )

    val selectedIndex = remember { mutableStateOf(0) }

    Window(onCloseRequest = ::exitApplication, title = "SVG demo (Compose Desktop)") {
        MaterialTheme {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                Row(modifier = Modifier.align(Alignment.CenterHorizontally), Arrangement.spacedBy(10.dp)) {
                    Text(items[selectedIndex.value].first, Modifier.align(Alignment.CenterVertically))
                    Button(modifier = Modifier.align(Alignment.CenterVertically),
                        onClick = {
                            selectedIndex.value++
                            if (selectedIndex.value == 3) {
                                selectedIndex.value = 0
                            }
                        }) {
                        Text("Next")
                    }
                }

                svg(
                    items[selectedIndex.value].second(),
                    modifier = Modifier.size(600.dp, 400.dp)
                )
            }
        }
    }
}

@Composable
private fun svg(
    svg: SvgSvgElement,
    modifier: Modifier = Modifier,
) {
    SwingPanel(
        modifier = modifier,
        factory = {
            SvgPanel(svg)
        }
    )
}