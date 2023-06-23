package org.jetbrains.letsPlot.skia.compose.android

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.plot.MonolithicCommon
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skia.android.MonolithicSkiaAndroid
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger
import org.jetbrains.letsPlot.skia.compose.util.PlotSizeUtil

private val LOG = NaiveLogger("PlotViewContainer")

@SuppressLint("ViewConstructor")
internal class PlotViewContainer(
    private val context: Context,
    private val computationMessagesHandler: ((List<String>) -> Unit)
) : RelativeLayout(context) {

    private var size = Vector.ZERO
    private lateinit var processedSpec: Map<String, Any>

    // updatable state
    var figure: Figure? = null
        set(fig) {
            check(fig != null) { "The 'figure' can't be null." }
            field = fig
            val rawSpec = fig.toSpec()
            processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        }

    var preserveAspectRatio: Boolean? = null
        set(v) {
            check(v != null) { "'preserveAspectRatio' value can't be null." }
            field = v
//            background = ColorRect(if (v) Color.BLUE else Color.RED)
        }

    init {
        LOG.print("New PlotViewContainer preserveAspectRatio: $preserveAspectRatio")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        LOG.print("onSizeChanged $w, $h PlotViewContainer preserveAspectRatio: $preserveAspectRatio")
        super.onSizeChanged(w, h, oldw, oldh)
        size = Vector(w, h)
        invalidatePlotView()
        revalidatePlotView()
    }

    fun revalidatePlotView() {
        LOG.print("revalidate $size PlotViewContainer preserveAspectRatio: $preserveAspectRatio")
        check(childCount == 0) { "Can't revalidate: childCount = $childCount but should be 0." }

        // This happens when `revalidate` is invoked from the initial `update` in `PlotPanel:AndroidView` composable.
        if (size.x == 0 || size.y == 0) return

        // https://stackoverflow.com/questions/25516363/how-to-properly-add-child-views-to-view-group
        post {
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

            val params = RelativeLayout.LayoutParams(width, height).also {
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

    fun invalidatePlotView() {
        disposePlotView()
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
