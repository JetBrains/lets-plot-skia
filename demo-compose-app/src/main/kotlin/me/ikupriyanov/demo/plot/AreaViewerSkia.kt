package me.ikupriyanov.demo.plot

import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import jetbrains.datalore.plotDemo.model.plotConfig.Area
import me.ikupriyanov.demo.utils.plot


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Area plot (Compose Desktop)") {
        MaterialTheme {
            plot(
                plotSpec = Area().plotSpecList().first(),
                modifier = Modifier.size(600.dp, 400.dp),
            )
        }
    }
}