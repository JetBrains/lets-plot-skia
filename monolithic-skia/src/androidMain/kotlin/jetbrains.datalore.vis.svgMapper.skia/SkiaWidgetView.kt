package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.view.ViewGroup

internal class SkiaWidgetView(
    context: Context,
    private val skiaWidget: SkiaWidget,
) : ViewGroup(context) {
    init {
        val width = skiaWidget.width()
        val height = skiaWidget.height()
        val density = resources.displayMetrics.density
        layoutParams = LayoutParams(
            (width * density).toInt(),
            (height * density).toInt()
        )
        skiaWidget.nativeLayer.attachTo(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val density = resources.displayMetrics.density
        val width = skiaWidget.width() * density
        val height = skiaWidget.height() * density
        setMeasuredDimension(width.toInt(), height.toInt())
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // FIXME: use child.measuredWidth/measuredHeight
        getChildAt(0).layout(0, 0, r - l, b - t)
    }
}
