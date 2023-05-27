package me.ikupriyanov.demo.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import jetbrains.datalore.vis.svgMapper.skia.plotComponent

@Composable
fun plot(
    plotSpec: MutableMap<String, Any>,
    modifier: Modifier = Modifier,
) {
    SwingPanel(
        modifier = modifier,
        factory = {
            plotComponent(plotSpec)
        }
    )
}
