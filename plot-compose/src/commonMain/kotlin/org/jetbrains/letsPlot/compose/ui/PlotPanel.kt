package org.jetbrains.letsPlot.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.letsPlot.Figure


@Composable
expect fun PlotPanel(
    figure: Figure,
    modifier: Modifier,
    computationMessagesHandler: ((List<String>) -> Unit)
)