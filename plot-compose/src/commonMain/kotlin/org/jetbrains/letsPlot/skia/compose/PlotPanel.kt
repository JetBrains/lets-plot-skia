package org.jetbrains.letsPlot.skia.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.letsPlot.Figure


@Suppress("FunctionName")
@Composable
expect fun PlotPanel(
    figure: Figure,
    modifier: Modifier,
    computationMessagesHandler: ((List<String>) -> Unit)
)