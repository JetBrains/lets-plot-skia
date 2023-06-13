package org.jetbrains.letsPlot.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.letsPlot.Figure


@Composable
actual fun PlotPanel(
    figure: Figure,
    modifier: Modifier,
    computationMessagesHandler: ((List<String>) -> Unit)

) {
//    val factory: () -> Component
//
//
//    SwingPanel<Component>(
//        background = Color.White,
//        factory = factory,
//        modifier = modifier,
//        update = NoOpUpdate
//    )
}

