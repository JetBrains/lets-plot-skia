/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.swing

import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.intern.toSpec
import javax.swing.JComponent

fun Figure.createComponent(
    preserveAspectRatio: Boolean = false,
    preferredSizeFromPlot: Boolean = false,
    repaintDelay: Int = 300,  // ms,
    computationMessagesHandler: (List<String>) -> Unit
): JComponent {
    val rawSpec = this.toSpec()
    val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)

    return PlotPanelSkiaSwing(
        processedSpec = processedSpec,
        preserveAspectRatio = preserveAspectRatio,
        preferredSizeFromPlot = preferredSizeFromPlot,
        repaintDelay = repaintDelay,
        computationMessagesHandler = computationMessagesHandler
    )
}