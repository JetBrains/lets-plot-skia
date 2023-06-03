package org.jetbrains.letsPlot.skiko.android

import jetbrains.datalore.vis.svg.SvgSvgElement
import org.jetbrains.letsPlot.skiko.SvgSkikoView
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoGestureEventKind

class SvgSkikoViewAndroid(
    svg: SvgSvgElement
) : SvgSkikoView(
    svg = svg
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