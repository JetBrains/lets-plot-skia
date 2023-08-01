package org.jetbrains.letsPlot.skia.awt.view

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.skia.svg.view.SkikoViewEventDispatcher
import org.jetbrains.letsPlot.skia.svg.view.SvgSkikoView
import org.jetbrains.skiko.SkiaLayer
import java.awt.Dimension

class SvgSkikoViewAwt(
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