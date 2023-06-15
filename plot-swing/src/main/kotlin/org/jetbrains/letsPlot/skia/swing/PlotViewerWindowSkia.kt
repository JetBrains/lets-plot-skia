/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.swing

import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.vis.swing.ApplicationContext
import jetbrains.datalore.vis.swing.DefaultPlotContentPane
import jetbrains.datalore.vis.swing.PlotPanel
import jetbrains.datalore.vis.swing.PlotViewerWindowBase
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skia.swing.AwtAppEnv.AWT_APP_CONTEXT
import java.awt.Dimension
import javax.swing.JComponent

class PlotViewerWindowSkia constructor(
    title: String,
    private val rawSpec: MutableMap<String, Any>,
    windowSize: Dimension? = null,
    private val preserveAspectRatio: Boolean = false,
    private val repaintDelay: Int = 300,  // ms,
) : PlotViewerWindowBase(
    title,
    windowSize = windowSize,
) {

    constructor(
        title: String,
        figure: Figure,
        windowSize: Dimension? = null,
        preserveAspectRatio: Boolean = false,
        repaintDelay: Int = 300,  // ms,
    ) : this(
        title = title,
        rawSpec = figure.toSpec(),
        windowSize = windowSize,
        preserveAspectRatio = preserveAspectRatio,
        repaintDelay = repaintDelay
    )

    override fun createWindowContent(preferredSizeFromPlot: Boolean): JComponent {
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        return object : DefaultPlotContentPane(
            processedSpec = processedSpec,
            preferredSizeFromPlot = preferredSizeFromPlot,
            repaintDelay = repaintDelay,
            applicationContext = AWT_APP_CONTEXT   // Not really used, can be a dummy.
        ) {
            override fun createPlotPanel(
                processedSpec: MutableMap<String, Any>,
                preferredSizeFromPlot: Boolean,
                repaintDelay: Int,
                applicationContext: ApplicationContext,
                computationMessagesHandler: (List<String>) -> Unit
            ): PlotPanel {
                return PlotPanelSkiaSwing(
                    processedSpec = processedSpec,
                    preserveAspectRatio = preserveAspectRatio,
                    preferredSizeFromPlot = preferredSizeFromPlot,
                    repaintDelay = repaintDelay,
                    computationMessagesHandler = computationMessagesHandler
                )
            }
        }
    }
}
