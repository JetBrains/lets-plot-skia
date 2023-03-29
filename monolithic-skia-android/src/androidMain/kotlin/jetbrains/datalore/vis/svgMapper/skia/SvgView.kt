package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.view.ViewGroup
import jetbrains.datalore.vis.svgMapper.skia.mapper.SvgSkiaWidget

internal class SvgView(
    context: Context,
    private val svgSkiaWidget: SvgSkiaWidget,
) : ViewGroup(context) {
    init {
        svgSkiaWidget.nativeLayer.attachTo(this)
        post { svgSkiaWidget.nativeLayer.needRedraw() }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val density = resources.displayMetrics.density
        val width = (svgSkiaWidget.width() * density).toInt()
        val height = (svgSkiaWidget.height() * density).toInt()

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
