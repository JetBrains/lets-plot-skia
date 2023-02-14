package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.widget.LinearLayout
import jetbrains.datalore.base.geometry.DoubleVector

internal class SkiaWidgetView(
    context: Context,
    skiaWidget: SkiaWidget,
    plotSize: DoubleVector?,
    plotMaxWidth: Double?,
) : LinearLayout(context) {
    init {
        val width = plotSize?.x ?: skiaWidget.width()
        val height = plotSize?.y ?: skiaWidget.height()
        val density = resources.displayMetrics.density
        layoutParams = LayoutParams(
            (width * density).toInt(),
            (height * density).toInt()
        )
        skiaWidget.nativeLayer.attachTo(this)
    }
}
