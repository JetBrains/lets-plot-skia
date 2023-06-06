/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skiko.swing

import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.vis.swing.ApplicationContext
import jetbrains.datalore.vis.swing.DefaultPlotContentPane
import jetbrains.datalore.vis.swing.PlotPanel
import jetbrains.datalore.vis.swing.PlotViewerWindowBase
import org.jetbrains.letsPlot.skiko.swing.AwtAppEnv.AWT_APP_CONTEXT
import java.awt.Dimension
import javax.swing.JComponent

class PlotViewerWindowSkiko(
    title: String,
    windowSize: Dimension? = null,
    private val rawSpec: MutableMap<String, Any>,
    private val preserveAspectRatio: Boolean = false,
    private val repaintDelay: Int = 300,  // ms,
    private val applicationContext: ApplicationContext = AWT_APP_CONTEXT  // Doesn't seem to be used // ToDo: fix in LP.
) : PlotViewerWindowBase(
    title,
    windowSize = windowSize,
) {

    override fun createWindowContent(preferredSizeFromPlot: Boolean): JComponent {
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        return object : DefaultPlotContentPane(
            processedSpec = processedSpec,
            preferredSizeFromPlot = preferredSizeFromPlot,
            repaintDelay = repaintDelay,
            applicationContext = applicationContext
        ) {
            override fun createPlotPanel(
                processedSpec: MutableMap<String, Any>,
                preferredSizeFromPlot: Boolean,
                repaintDelay: Int,
                applicationContext: ApplicationContext,
                computationMessagesHandler: (List<String>) -> Unit
            ): PlotPanel {
                return PlotPanelSkiko(
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
