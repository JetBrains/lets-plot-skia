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
        super.onSizeChanged(w, h, oldw, oldh)

        // https://stackoverflow.com/questions/25516363/how-to-properly-add-child-views-to-view-group
        post {
            removeAllViews()
            val density = resources.displayMetrics.density
            val scaledSize = DoubleVector(w.toDouble(), h.toDouble()).mul(1.0 / density)
            val svgView = plotComponentProvider.createComponent(context, scaledSize)
            addView(svgView, w, h)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val preferredSize = plotComponentProvider.getPreferredSize(DoubleVector(width.toDouble(), height.toDouble()))

        if (childCount > 0) {
            measureChild(
                getChildAt(0),
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
        }
        setMeasuredDimension(preferredSize.x.toInt(), preferredSize.y.toInt())
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount > 0) {
            getChildAt(0).layout(0, 0, measuredWidth, measuredHeight)
        }
    }
}
