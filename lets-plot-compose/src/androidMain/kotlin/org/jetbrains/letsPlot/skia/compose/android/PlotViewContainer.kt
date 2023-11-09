/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose.android

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.PlotSizeUtil
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skia.android.MonolithicSkiaAndroid
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger

private val LOG = NaiveLogger("PlotViewContainer")

@SuppressLint("ViewConstructor")
internal class PlotViewContainer(
    private val context: Context,
    private val computationMessagesHandler: ((List<String>) -> Unit)
) : RelativeLayout(context) {

    private var needUpdate = true
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
        disposePlotView()

        // https://stackoverflow.com/questions/25516363/how-to-properly-add-child-views-to-view-group
        post {
            check(childCount == 0) { "Can't revalidate: childCount = $childCount but should be 0." }

            val w = size.x
            val h = size.y

            val density = resources.displayMetrics.density
            val scaledSize = DoubleVector(w.toDouble(), h.toDouble()).mul(1.0 / density)
            val (bounds, plotView) = buildPlotComponent(scaledSize)

            val unscaledOrigin = bounds.origin.mul(density.toDouble())
            val unscaledSize = bounds.dimension.mul(density.toDouble())

            val left = unscaledOrigin.x.toInt()
            val top = unscaledOrigin.y.toInt()
            val width = unscaledSize.x.toInt()
            val height = unscaledSize.y.toInt()

            val params = LayoutParams(width, height).also {
                it.leftMargin = left
                it.topMargin = top
            }

            addView(plotView, params)
//            this.requestLayout()
//            this.forceLayout()

            // Add measure/layout plotView.
            // Without this trick Skia layer do not re-draw after thw screen (device) rotation.
            measureChild(
                plotView,
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
            plotView.layout(left, top, left + width, top + height)
        }
    }

    private fun buildPlotComponent(containerSize: DoubleVector): Pair<DoubleRectangle, View> {

        val plotSize = PlotSizeUtil.preferredFigureSize(
            processedSpec,
            preserveAspectRatio!!,
            containerSize
        )

        val plotX = if (plotSize.x >= containerSize.x) 0.0 else {
            (containerSize.x - plotSize.x) / 2
        }
        val plotY = if (plotSize.y >= containerSize.y) 0.0 else {
            (containerSize.y - plotSize.y) / 2
        }

        val bounds = DoubleRectangle(
            DoubleVector(plotX, plotY),
            plotSize
        )

        val plotComponent = MonolithicSkiaAndroid.buildPlotFromProcessedSpecs(
            ctx = context,
            plotSize = plotSize,
            plotSpec = processedSpec as MutableMap<String, Any>,
        ) { messages ->
            computationMessagesHandler(messages)
        }

        return Pair(bounds, plotComponent)
    }

    fun disposePlotView() {
        for (ind in 0 until childCount) {
            getChildAt(ind).let {
                if (it is Disposable) {
                    it.dispose()
                }
            }
        }

        removeAllViews()
    }
}
