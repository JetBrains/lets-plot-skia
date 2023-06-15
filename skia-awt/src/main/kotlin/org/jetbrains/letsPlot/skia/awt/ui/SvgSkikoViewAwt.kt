package org.jetbrains.letsPlot.skia.awt.ui

import jetbrains.datalore.vis.svg.SvgSvgElement
import org.jetbrains.letsPlot.skia.skiko.SkikoViewEventDispatcher
import org.jetbrains.letsPlot.skia.skiko.SvgSkikoView
import org.jetbrains.skiko.SkiaLayer
import java.awt.Dimension

class SvgSkikoViewAwt constructor(
    svg: SvgSvgElement,
    eventDispatcher: SkikoViewEventDispatcher?
) : SvgSkikoView(
    svg = svg,
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