package org.jetbrains.letsPlot.skia.android.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.DisposableRegistration
import jetbrains.datalore.base.registration.DisposingHub
import jetbrains.datalore.vis.svg.SvgSvgElement
import org.jetbrains.letsPlot.skia.skiko.SkikoViewEventDispatcher

@SuppressLint("ViewConstructor")
class SvgPanel constructor(
    context: Context,
    svg: SvgSvgElement,
    eventDispatcher: SkikoViewEventDispatcher? = null
) : ViewGroup(context), Disposable, DisposingHub {

    private val skikoView = SvgSkikoViewAndroid(svg, eventDispatcher)
    private val registrations = CompositeRegistration()

    val eventDispatcher: SkikoViewEventDispatcher
        get() {
            return skikoView.eventDispatcher ?: throw IllegalStateException("No SkikoViewEventDispatcher.")
        }

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

    override fun registerDisposable(disposable: Disposable) {
        registrations.add(DisposableRegistration(disposable))
    }

    override fun dispose() {
        registrations.dispose()
        skikoView.dispose()
        removeAllViews()
    }
}
