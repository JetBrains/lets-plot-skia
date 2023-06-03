package org.jetbrains.letsPlot.skiko.android

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import jetbrains.datalore.vis.svg.SvgSvgElement

// ToDoL dispose ?
@SuppressLint("ViewConstructor")
class SvgPanelAndroid(
    context: Context,
    svg: SvgSvgElement
) : ViewGroup(context) {

    private val skikoView = SvgSkikoViewAndroid(svg)

    init {
        skikoView.skiaLayer.attachTo(this)

        post {
            skikoView.skiaLayer.needRedraw()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val density = resources.displayMetrics.density
        val width = (skikoView.width * density).toInt()
        val height = (skikoView.height * density).toInt()

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
