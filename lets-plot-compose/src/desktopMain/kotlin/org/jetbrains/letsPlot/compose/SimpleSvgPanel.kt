/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.compose.desktop.SvgView
import org.jetbrains.letsPlot.compose.desktop.SvgViewPanel

/**
 * Renders SVG content using Compose Canvas and Skia.
 *
 * For demo and testing purposes only.
 */
@Composable
fun SimpleSvgPanel(
    svg: SvgSvgElement,
    modifier: Modifier = Modifier
) {
    // Create and manage the SvgSkikoView
    val svgView = remember { SvgView() }

    svgView.svg = svg

    // Dispose the view when leaving composition
    DisposableEffect(svgView) {
        onDispose {
            svgView.dispose()
        }
    }

    SvgViewPanel(
        svgView = svgView,
        modifier = modifier
    )
}