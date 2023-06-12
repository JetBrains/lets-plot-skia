package org.jetbrains.letsPlot.skiko.android

import jetbrains.datalore.vis.svg.SvgSvgElement
import org.jetbrains.letsPlot.skiko.SkikoViewEventDispatcher
import org.jetbrains.letsPlot.skiko.SvgSkikoView
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoGestureEventKind

class SvgSkikoViewAndroid constructor(
    svg: SvgSvgElement,
    eventDispatcher: SkikoViewEventDispatcher?
) : SvgSkikoView(
    svg = svg,
    eventDispatcher = eventDispatcher
) {
    override fun createSkiaLayer(view: SvgSkikoView): SkiaLayer {
        return SkiaLayer().also {
//            it.preferredSize = Dimension(view.width, view.height)
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