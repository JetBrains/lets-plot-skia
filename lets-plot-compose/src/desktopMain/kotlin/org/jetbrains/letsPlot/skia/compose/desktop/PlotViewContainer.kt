/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose.desktop

import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.PlotSizeUtil
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skia.awt.view.SvgPanel
import org.jetbrains.letsPlot.skia.builderLW.MonolithicSkiaLW
import org.jetbrains.letsPlot.skia.builderLW.ViewModel
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger
import java.awt.Cursor
import java.awt.Rectangle
import javax.swing.JPanel

private val LOG = NaiveLogger("PlotViewContainer")

class PlotViewContainer(
    private val computationMessagesHandler: ((List<String>) -> Unit)
) : JPanel() {

    private lateinit var plotSvgPanel: SvgPanel
    private var viewModel: ViewModel? = null

    private var needUpdate = false
    private var dispatchComputationMessages = true
    private lateinit var processedSpec: Map<String, Any>

    var figure: Figure? = null
        set(fig) {
            check(fig != null) { "The 'figure' can't be null." }

            if (field == fig) {
                return
            }

            field = fig
            needUpdate = true

            val rawSpec = fig.toSpec()
            processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        }

    var preserveAspectRatio: Boolean? = null
        set(v) {
            check(v != null) { "'preserveAspectRatio' value can't be null." }

            if (field == v) {
                return
            }

            // TODO: investigate and report the bug, most likely in Skiko.
            // Switching the aspect ratio causes flickering - extended plot area is rendered with black background.
            rebuildSvgPanel()
            field = v
            needUpdate = true
        }

    var size: DoubleVector = DoubleVector.ZERO
        set(v) {
            if (field == v) {
                return
            }

            // TODO: investigate and report the bug, most likely in Skiko.
            // With fixed aspect ratio the horizontal resize causes the plot to be rendered at wrong position, even outside the panel.
            rebuildSvgPanel()
            field = v
            needUpdate = true
        }

    init {
        LOG.print("New PlotViewContainer preserveAspectRatio: $preserveAspectRatio")
        isOpaque = false
        layout = null
        cursor = Cursor(Cursor.CROSSHAIR_CURSOR)
        rebuildSvgPanel()
    }

    fun updatePlotView() {
        LOG.print("updatePlotView() - needUpdate: $needUpdate, preserveAspectRatio: $preserveAspectRatio size: $size")

        if (!needUpdate) {
            return
        }

        needUpdate = false

        val plotSize = PlotSizeUtil.preferredFigureSize(
            processedSpec,
            preserveAspectRatio ?: throw IllegalStateException("'preserveAspectRatio' not set."),
            size
        )

        val plotX = if (plotSize.x >= size.x) 0.0 else {
            (size.x - plotSize.x) / 2
        }
        val plotY = if (plotSize.y >= size.y) 0.0 else {
            (size.y - plotSize.y) / 2
        }

        plotSvgPanel.bounds = Rectangle(
            plotX.toInt(),
            plotY.toInt(),
            plotSize.x.toInt(),
            plotSize.y.toInt(),
        )

        viewModel?.dispose()
        viewModel = MonolithicSkiaLW.buildPlotFromProcessedSpecs(
            plotSpec = processedSpec as MutableMap<String, Any>,
            plotSize = plotSize,
        ) { messages ->
            if (dispatchComputationMessages) {
                // do once
                dispatchComputationMessages = false
                computationMessagesHandler(messages)
            }
        }
        plotSvgPanel.svg = viewModel!!.svg
        plotSvgPanel.eventDispatcher = viewModel!!.eventDispatcher
    }

    fun disposePlotView() {
        check(componentCount == 1) { "Unexpected number of children: $componentCount" }
        check(components[0] == plotSvgPanel) { "Unexpected child: should be SvgPanel but was ${components[0]::class.simpleName}" }

        removeAll()
        plotSvgPanel.dispose()
        viewModel?.dispose()
    }

    private fun rebuildSvgPanel() {
        if (componentCount == 1) {
            removeAll()
            plotSvgPanel.dispose()
            viewModel?.dispose()
        }

        plotSvgPanel = SvgPanel()
        viewModel = null
        add(plotSvgPanel)
    }
}
