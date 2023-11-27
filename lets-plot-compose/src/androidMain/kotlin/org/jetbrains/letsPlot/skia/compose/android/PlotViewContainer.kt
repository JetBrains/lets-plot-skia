/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose.android

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.PlotSizeUtil
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skia.android.view.SvgPanel
import org.jetbrains.letsPlot.skia.builderLW.MonolithicSkiaLW
import org.jetbrains.letsPlot.skia.builderLW.ViewModel
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger

private val LOG = NaiveLogger("PlotViewContainer")

@SuppressLint("ViewConstructor")
internal class PlotViewContainer(
    context: Context,
    private val computationMessagesHandler: ((List<String>) -> Unit)
) : RelativeLayout(context) {

    private lateinit var plotSvgPanel: SvgPanel
    private var viewModel: ViewModel? = null

    private var needUpdate = true
    private lateinit var processedSpec: Map<String, Any>

    init {
        rebuildSvgPanel()
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

            // TODO: investigate. Most likely a Skiko bug.
            // Switching the aspect ratio causes flickering - extended plot area is rendered with black background.
            rebuildSvgPanel()
            field = v
            needUpdate = true
//            background = ColorRect(if (v) Color.BLUE else Color.RED)
        }

    private var size = Vector.ZERO
        set(value) {
            if (field == value) {
                return
            }
            field = value
            needUpdate = true
        }


    init {
        LOG.print("New PlotViewContainer preserveAspectRatio: $preserveAspectRatio")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        LOG.print("onSizeChanged $w, $h PlotViewContainer preserveAspectRatio: $preserveAspectRatio")
        super.onSizeChanged(w, h, oldw, oldh)

        size = Vector(w, h)
        updatePlotView()
    }

    fun updatePlotView() {
        LOG.print("updatePlotView() - needUpdate: $needUpdate, preserveAspectRatio: $preserveAspectRatio size: $size")

        // This happens when `revalidate` is invoked from the initial `update` in `PlotPanel:AndroidView` composable.
        if (size.x == 0 || size.y == 0) return
        if (!needUpdate) {
            return
        }
        needUpdate = false

        val w = size.x
        val h = size.y

        val density = resources.displayMetrics.density
        val scaledSize = DoubleVector(w.toDouble(), h.toDouble()).mul(1.0 / density)
        val plotSize = PlotSizeUtil.preferredFigureSize(
            processedSpec,
            preserveAspectRatio!!,
            scaledSize
        )
        val plotX = if (plotSize.x >= scaledSize.x) 0.0 else {
            (scaledSize.x - plotSize.x) / 2
        }
        val plotY = if (plotSize.y >= scaledSize.y) 0.0 else {
            (scaledSize.y - plotSize.y) / 2
        }
        val bounds = DoubleRectangle(
            DoubleVector(plotX, plotY),
            plotSize
        )

        val unscaledOrigin = bounds.origin.mul(density.toDouble())
        val unscaledSize = bounds.dimension.mul(density.toDouble())

        val left = unscaledOrigin.x.toInt()
        val top = unscaledOrigin.y.toInt()
        val width = unscaledSize.x.toInt()
        val height = unscaledSize.y.toInt()

        post {
            viewModel?.dispose()
            viewModel = MonolithicSkiaLW.buildPlotFromProcessedSpecs(
                plotSize = plotSize,
                plotSpec = processedSpec as MutableMap<String, Any>,
            ) { messages ->
                computationMessagesHandler(messages)
                //if (dispatchComputationMessages) {
                //    // do once
                //    dispatchComputationMessages = false
                //    computationMessagesHandler(messages)
                //}
            }

            plotSvgPanel.svg = viewModel!!.svg
            plotSvgPanel.eventDispatcher = viewModel!!.eventDispatcher

            measureChild(
                plotSvgPanel,
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
            plotSvgPanel.layout(left, top, left + width, top + height)
        }
    }

    fun disposePlotView() {
        check(childCount == 1) { "Unexpected number of children: $childCount" }
        check(getChildAt(0) == plotSvgPanel) { "Unexpected child: should be SvgPanel but was ${getChildAt(0)::class.simpleName}" }

        removeAllViews()
        plotSvgPanel.dispose()
        viewModel?.dispose()
    }

    private fun rebuildSvgPanel() {
        if (childCount == 1) {
            removeAllViews()
            plotSvgPanel.dispose()
            viewModel?.dispose()
        }

        plotSvgPanel = SvgPanel(context)
        viewModel = null
        addView(plotSvgPanel)
    }
}
