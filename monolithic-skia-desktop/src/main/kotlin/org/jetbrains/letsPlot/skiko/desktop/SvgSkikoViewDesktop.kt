package org.jetbrains.letsPlot.skiko.desktop

import jetbrains.datalore.vis.svg.SvgSvgElement
import org.jetbrains.letsPlot.skiko.SvgSkikoView
import org.jetbrains.skiko.SkiaLayer
import java.awt.Dimension

class SvgSkikoViewDesktop(
    svg: SvgSvgElement
) : SvgSkikoView(
    svg = svg
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