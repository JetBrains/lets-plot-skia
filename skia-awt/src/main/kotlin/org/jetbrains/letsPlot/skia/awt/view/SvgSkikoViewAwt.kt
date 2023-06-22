package org.jetbrains.letsPlot.skia.awt.view

import jetbrains.datalore.vis.svg.SvgSvgElement
import org.jetbrains.letsPlot.skia.svg.view.SkikoViewEventDispatcher
import org.jetbrains.letsPlot.skia.svg.view.SvgSkikoView
import org.jetbrains.skiko.SkiaLayer
import java.awt.Dimension

/**
 * Note about `handleSkikoEvents = false`:
 *
 *      In Compose env SkikoView DOESN'T receive SkikoGestureEvent etc.
 *      In Swing env SkikoView DOES receive SkikoGestureEvent etc.
 *
 *      Because we can't rely on Skiko events in SkikoView let's disable their handling altogether.
 *
 *      June 22 '23, Skiko v.0.7.63
 */
class SvgSkikoViewAwt constructor(
    svg: SvgSvgElement,
    handleSkikoEvents: Boolean,
    eventDispatcher: SkikoViewEventDispatcher?
) : SvgSkikoView(
    svg = svg,
    handleSkikoEvents = handleSkikoEvents,
    eventDispatcher = eventDispatcher
) {
    override fun createSkiaLayer(view: SvgSkikoView): SkiaLayer {
        return SkiaLayer().also {
            it.preferredSize = Dimension(view.width, view.height)

            // https://github.com/JetBrains/skiko/issues/614
            //skiaLayer.skikoView = skikoView
            it.addView(view)
        }
    }
}