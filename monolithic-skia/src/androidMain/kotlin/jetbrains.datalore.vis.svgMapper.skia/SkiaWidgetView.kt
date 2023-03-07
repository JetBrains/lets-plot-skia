package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.view.ViewGroup

internal class SkiaWidgetView(
    context: Context,
    private val skiaWidget: SkiaWidget,
) : ViewGroup(context) {
    init {
        skiaWidget.nativeLayer.attachTo(this)
        post { skiaWidget.nativeLayer.needRedraw() }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val density = resources.displayMetrics.density
        val width = (skiaWidget.width() * density).toInt()
        val height = (skiaWidget.height() * density).toInt()

        measureChild(
            getChildAt(0),
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        getChildAt(0).apply {
            layout(0, 0, measuredWidth, measuredHeight)
        }
    }
}
