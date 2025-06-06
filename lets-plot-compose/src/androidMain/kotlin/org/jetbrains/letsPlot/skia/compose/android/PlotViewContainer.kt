/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose.android

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.PlotSizeHelper
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skia.builderLW.MonolithicSkiaLW
import org.jetbrains.letsPlot.skia.builderLW.ViewModel
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger

private val LOG = NaiveLogger("PlotViewContainer")

@SuppressLint("ViewConstructor")
internal class PlotViewContainer(
    context: Context,
    private val computationMessagesHandler: ((List<String>) -> Unit)
) : RelativeLayout(context) {

    private lateinit var plotSvgPanel: SvgCanvasView
    private var viewModel: ViewModel? = null

    private lateinit var processedSpec: Map<String, Any>

    private var disposed = false

    var figure: Figure? = null
        set(fig) {
            check(fig != null) { "The 'figure' can't be null." }

            if (field == fig) {
                return
            }

            field = fig

            val rawSpec = fig.toSpec()
            processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
            updatePlotView()
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

            updatePlotView()
//            background = ColorRect(if (v) Color.BLUE else Color.RED)
        }

    private var size = Vector.ZERO
        set(value) {
            if (field == value) {
                return
            }
            field = value

            updatePlotView()
        }


    init {
        LOG.print { "New PlotViewContainer preserveAspectRatio: $preserveAspectRatio" }

        rebuildSvgPanel()

        // Make plot visible on the first render.
        post {
            measureChild(
                plotSvgPanel,
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
            plotSvgPanel.layout(left, top, left + width, top + height)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        LOG.print { "onSizeChanged $w, $h PlotViewContainer preserveAspectRatio: $preserveAspectRatio" }
        super.onSizeChanged(w, h, oldw, oldh)

        // Crash on rotation. Do not call `updatePlotView()` (internally called from `size` property setter).
        if (disposed) return
        size = Vector(w, h)
    }

    private fun updatePlotView() {
        LOG.print { "updatePlotView() - preserveAspectRatio: $preserveAspectRatio size: $size" }

        check(!disposed) { "PlotViewContainer is disposed." }

        // This happens when `revalidate` is invoked from the initial `update` in `PlotPanel:AndroidView` composable.
        if (size.x == 0 || size.y == 0) return

        val w = size.x
        val h = size.y

        val density = resources.displayMetrics.density
        val scaledSize = DoubleVector(w.toDouble(), h.toDouble()).mul(1.0 / density)
        val plotSize = PlotSizeHelper.singlePlotSize(
            plotSpec = processedSpec,
            containerSize = scaledSize,
            sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio!!),
            facets = PlotFacets.UNDEFINED,
            containsLiveMap = false
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

        viewModel?.dispose()
        viewModel = MonolithicSkiaLW.buildPlotFromProcessedSpecs(
            plotSpec = processedSpec as MutableMap<String, Any>,
            containerSize = null,
            sizingPolicy = SizingPolicy.fixed(plotSize.x, plotSize.y),
        ) { messages ->
            computationMessagesHandler(messages)
            //if (dispatchComputationMessages) {
            //    // do once
            //    dispatchComputationMessages = false
            //    computationMessagesHandler(messages)
            //}
        }

        plotSvgPanel.svg = viewModel!!.svg
        plotSvgPanel.eventDispatcher = object : CanvasEventDispatcher {
            override fun addEventHandler(
                eventSpec: MouseEventSpec,
                eventHandler: EventHandler<MouseEvent>
            ): Registration {
                return viewModel!!.eventDispatcher.addEventHandler(eventSpec, eventHandler)
            }

            override fun dispatchMouseEvent(
                kind: MouseEventSpec,
                e: MouseEvent
            ) {
                viewModel!!.eventDispatcher.dispatchMouseEvent(kind, e)
            }

        }
            viewModel!!.eventDispatcher

        measureChild(
            plotSvgPanel,
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        plotSvgPanel.layout(left, top, left + width, top + height)
    }

    fun disposePlotView() {
        LOG.print { "disposePlotView()" }
        check(childCount == 1) { "Unexpected number of children: $childCount" }
        check(getChildAt(0) == plotSvgPanel) { "Unexpected child: should be SvgPanel but was ${getChildAt(0)::class.simpleName}" }

        disposed = true

        clearContainer()
    }

    private fun rebuildSvgPanel() {
        LOG.print { "rebuildSvgPanel()" }

        if (childCount == 1) {
            clearContainer()
        }

        plotSvgPanel = SvgCanvasView(context)
        viewModel = null
        addView(plotSvgPanel)
    }

    /**
     * Looks likely Skiko tries to finish rendering tasks in a same frame where the plot was disposed.
     * Deferring plot panel disposing to the next frame seems to fix the error.

     * See:
     * Android: fast plot rebuild can cause a crash (https://github.com/JetBrains/lets-plot-skia/issues/9)
     */
    private fun clearContainer() {
        removeAllViews()

        // Copy for closure
        val panel = plotSvgPanel
        val vm = viewModel

        post {
            panel.dispose()
            vm?.dispose()
        }
    }
}
