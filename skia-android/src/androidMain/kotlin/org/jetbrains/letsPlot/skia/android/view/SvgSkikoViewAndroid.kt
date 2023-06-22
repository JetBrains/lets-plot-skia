package org.jetbrains.letsPlot.skia.android.view

import jetbrains.datalore.vis.svg.SvgSvgElement
import org.jetbrains.letsPlot.skia.svg.view.SkikoViewEventDispatcher
import org.jetbrains.letsPlot.skia.svg.view.SvgSkikoView
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoGestureEventKind

class SvgSkikoViewAndroid constructor(
    svg: SvgSvgElement,
    eventDispatcher: SkikoViewEventDispatcher?
) : SvgSkikoView(
    svg = svg,
    handleSkikoEvents = true,    // On Android, SkikoView receives SkikoGestureEvent etc.
    eventDispatcher = eventDispatcher
) {
    override fun createSkiaLayer(view: SvgSkikoView): SkiaLayer {
        return SkiaLayer().also {
            it.gesturesToListen = arrayOf(
                SkikoGestureEventKind.PAN,
                SkikoGestureEventKind.DOUBLETAP,
                SkikoGestureEventKind.TAP,
                SkikoGestureEventKind.LONGPRESS
            )
            it.skikoView = view
        }
    }
}