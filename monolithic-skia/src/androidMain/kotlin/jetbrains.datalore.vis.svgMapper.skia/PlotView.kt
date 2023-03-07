package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.view.ViewGroup
import jetbrains.datalore.base.geometry.DoubleVector

//DefaultPlotPanelBatik
class PlotView(
    context: Context,
    processedSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean,
    computationMessagesHandler: (List<String>) -> Unit
) : ViewGroup(context) {
    private val plotComponentProvider: PlotComponentProvider =
        PlotSpecComponentProvider(processedSpec, preserveAspectRatio, computationMessagesHandler)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val density = resources.displayMetrics.density
        super.onSizeChanged(w, h, oldw, oldh)
        removeAllViews()
        val scaledSize = DoubleVector(w.toDouble(), h.toDouble()).mul(1.0 / density)
        val svgView = plotComponentProvider.createComponent(context, scaledSize)
        addView(svgView, w, h)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec).toDouble()

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec).toDouble()

        val preferredSize = plotComponentProvider.getPreferredSize(DoubleVector(width, height))

        setMeasuredDimension(preferredSize.x.toInt(), preferredSize.y.toInt())
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount > 0) {
            // FIXME: use child.measuredWidth/measuredHeight
            getChildAt(0).layout(0, 0, r - l, b - t)
        }
    }
}
