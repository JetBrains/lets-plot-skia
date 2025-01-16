/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.util

import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.raster.builderLW.MonolithicSkiaLW
import javax.swing.JComponent

fun Figure.createCanvas(
    preserveAspectRatio: Boolean = false,
    preferredSizeFromPlot: Boolean = false,
    repaintDelay: Int = 300,  // ms,
    computationMessagesHandler: (List<String>) -> Unit
): JComponent {
    val rawSpec = this.toSpec()
    val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
    val vm = MonolithicSkiaLW.buildPlotFromProcessedSpecs(
        processedSpec,
        plotSize = null,
        computationMessagesHandler
    )

    val view = SwingSvgCanvasView().apply {
        this.eventDispatcher = vm.eventDispatcher
        this.svg = vm.svg
    }

    return view.container
}
