package org.jetbrains.letsPlot.skia.compose.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.View
import android.widget.RelativeLayout
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.plot.MonolithicCommon
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skia.android.MonolithicSkiaAndroid
import org.jetbrains.letsPlot.skia.compose.util.PlotSizeUtil

class PlotComponentProvider(
    figure: Figure,
    private val preserveAspectRatio: Boolean,
    private val computationMessagesHandler: ((List<String>) -> Unit)
) {
    private var providedView: PlotViewContainer? = null

    val factory: (Context) -> View = { ctx ->
        check(providedView == null) { "An attempt to reuse a single-use view factory." }
        PlotViewContainer(
            ctx,
            figure,
            preserveAspectRatio,
            computationMessagesHandler
        ).also {
            providedView = it
        }
    }

    fun onDispose() {
        providedView?.disposeInner()
        providedView = null
    }
}

@SuppressLint("ViewConstructor")
private class PlotViewContainer(
    private val context: Context,
    figure: Figure,
    private val preserveAspectRatio: Boolean,
    private val computationMessagesHandler: ((List<String>) -> Unit)
) : RelativeLayout(context) {

    private var dispatchComputationMessages = true
    private val processedSpec: Map<String, Any>

    init {
//        background = object : Drawable() {
//            override fun draw(canvas: Canvas) {
//                canvas.drawRect(
//                    canvas.clipBounds,
//                    Paint().also { it.color = Color.BLUE }
//                )
//            }
//
//            override fun setAlpha(alpha: Int) {
//            }
//
//            override fun setColorFilter(colorFilter: ColorFilter?) {
//            }
//
//            override fun getOpacity(): Int {
//                return PixelFormat.OPAQUE
//            }
//        }

        val rawSpec = figure.toSpec()
        processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // https://stackoverflow.com/questions/25516363/how-to-properly-add-child-views-to-view-group
        post {
            disposeInner()

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
            preserveAspectRatio,
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
            if (dispatchComputationMessages) {
                // Dispatch just once.
                dispatchComputationMessages = false
                computationMessagesHandler(messages)
            }
        }

        return Pair(bounds, plotComponent)
    }


    fun disposeInner() {
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