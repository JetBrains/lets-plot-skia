package me.ikupriyanov.demo.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.skia.plotComponent
import jetbrains.datalore.vis.svgMapper.skia.svgComponent

@Composable
fun plot(
    processedSpec: MutableMap<String, Any>,
    modifier: Modifier = Modifier,
) {
    SwingPanel(
        modifier = modifier,
        factory = {
            plotComponent(processedSpec)
        }
    )
}

@Composable
fun svg(
    svg: SvgSvgElement,
    modifier: Modifier = Modifier,
) {
    SwingPanel(
        modifier = modifier,
        factory = {
            svgComponent(svg)
        }
    )
}