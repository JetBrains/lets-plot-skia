/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.swing.skia

import jetbrains.datalore.vis.swing.PlotPanel
import org.jetbrains.letsPlot.swing.skia.AwtAppEnv.AWT_APP_CONTEXT

open class PlotPanelSkiaSwing(
    processedSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean,
    preferredSizeFromPlot: Boolean,
    repaintDelay: Int,  // ms,
    computationMessagesHandler: (List<String>) -> Unit
) : PlotPanel(
    plotComponentProvider = PlotComponentProviderSkiaSwing(
        processedSpec = processedSpec,
        preserveAspectRatio = preserveAspectRatio,
        computationMessagesHandler = computationMessagesHandler
    ),
    preferredSizeFromPlot = preferredSizeFromPlot,
    repaintDelay = repaintDelay,
    applicationContext = AWT_APP_CONTEXT
)