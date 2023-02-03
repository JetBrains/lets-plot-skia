package me.ikupriyanov.demo.svg

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
import jetbrains.datalore.vis.svgMapper.common.DemoModelA
import jetbrains.datalore.vis.svgMapper.common.DemoModelB
import jetbrains.datalore.vis.svgMapper.common.DemoModelC
import me.ikupriyanov.demo.utils.svg

fun main() = application {
    val items = listOf(
        "SVG model A" to DemoModelA::createModel,
        "SVG model B" to DemoModelB::createModel,
        "SVG model C" to DemoModelC::createModel
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