/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose.desktop

import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.PlotSizeUtil
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skia.awt.MonolithicSkiaAwt
import org.jetbrains.letsPlot.skia.awt.view.SvgPanel
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger
import java.awt.Cursor
import java.awt.Rectangle
import javax.swing.JPanel

private val LOG = NaiveLogger("PlotViewContainer")

// If false then new SvgPanel() is created for each update.
val reusePlotSvgPanel = true

class PlotViewContainer(
    private val computationMessagesHandler: ((List<String>) -> Unit)
) : JPanel() {

    private val plotSvgPanel = SvgPanel()
    private var plotSvgRegistration = Registration.EMPTY

    private var needUpdate = false
    private var dispatchComputationMessages = true
    private lateinit var processedSpec: Map<String, Any>

    init {
        if (reusePlotSvgPanel) {
            this.add(plotSvgPanel)
        }
    }

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
            field = v
            needUpdate = true
        }

    var size: DoubleVector = DoubleVector.ZERO
        set(v) {
            if (field == v) {
                return
            }
            field = v
            needUpdate = true
        }

    init {
        LOG.print("New PlotViewContainer preserveAspectRatio: $preserveAspectRatio")
        isOpaque = false
        layout = null
        cursor = Cursor(Cursor.CROSSHAIR_CURSOR)
    }

    fun updatePlotView() {
        LOG.print("updatePlotView() - needUpdate: $needUpdate, preserveAspectRatio: $preserveAspectRatio size: $size")

        if (!needUpdate) {
            return
        }

        needUpdate = false

        if (!reusePlotSvgPanel) {
            disposePlotView()
        }

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

        if (reusePlotSvgPanel) {
            plotSvgRegistration.dispose()
            plotSvgPanel.bounds = Rectangle(
                plotX.toInt(),
                plotY.toInt(),
                plotSize.x.toInt(),
                plotSize.y.toInt(),
            )

            plotSvgRegistration = MonolithicSkiaAwt.buildPlotFromProcessedSpecs(
                svgPanel = plotSvgPanel,
                plotSize = plotSize,
                plotSpec = processedSpec as MutableMap<String, Any>,
            ) { messages ->
                if (dispatchComputationMessages) {
                    // do once
                    dispatchComputationMessages = false
                    computationMessagesHandler(messages)
                }
            }
        } else {
            val plotComponent = MonolithicSkiaAwt.buildPlotFromProcessedSpecs(
                plotSize = plotSize,
                plotSpec = processedSpec as MutableMap<String, Any>,
            ) { messages ->
                if (dispatchComputationMessages) {
                    // do once
                    dispatchComputationMessages = false
                    computationMessagesHandler(messages)
                }
            }

            plotComponent.bounds = Rectangle(
                plotX.toInt(),
                plotY.toInt(),
                plotSize.x.toInt(),
                plotSize.y.toInt(),
            )
            this.add(plotComponent)
        }
    }


    fun disposePlotView() {
        for (component in components) {
            if (component is Disposable) {
                component.dispose()
            }
        }
        removeAll()

        if (reusePlotSvgPanel) {
            plotSvgRegistration.dispose()
        }
    }
}