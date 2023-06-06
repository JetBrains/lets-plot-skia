/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.util

import jetbrains.datalore.plot.MonolithicCommon
import org.jetbrains.letsPlot.skiko.swing.PlotPanelSkiko
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import javax.swing.JComponent

class PlotSpecsDemoWindow(
    title: String,
    specList: List<MutableMap<String, Any>>,
    maxCol: Int = 3,
    plotSize: Dimension? = null,
    background: Color = Color.WHITE
) : PlotSpecsDemoWindowBase(
    title,
    specList = specList,
    maxCol = maxCol,
    plotSize = plotSize,
    background = background
) {

    override fun createPlotComponent(rawSpec: MutableMap<String, Any>, plotSize: Dimension?): JComponent {
        // Pre-process figure specifications
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        val plotPanel = PlotPanelSkiko(
            processedSpec = processedSpec,
            preferredSizeFromPlot = plotSize == null,
            repaintDelay = 300,
            preserveAspectRatio = false,
        ) { messages ->
            for (message in messages) {
                println("[Demo Plot Viewer] $message")
            }
        }

        plotSize?.let {
            plotPanel.preferredSize = it
        }

        plotPanel.alignmentX = Component.CENTER_ALIGNMENT
        return plotPanel
    }
}