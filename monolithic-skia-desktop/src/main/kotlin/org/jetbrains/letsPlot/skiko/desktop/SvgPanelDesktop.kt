package org.jetbrains.letsPlot.skiko.desktop

import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.vis.svg.SvgSvgElement
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.SwingUtilities

class SvgPanelDesktop(
    svg: SvgSvgElement
) : JPanel(), Disposable {

    private val skikoView = SvgSkikoViewDesktop(svg)

    init {
        layout = GridLayout(0, 1, 0, 0)
        border = null // BorderFactory.createLineBorder(Color.ORANGE, 1)
//        svgSkiaWidget.nativeLayer.attachTo(this)
        skikoView.skiaLayer.attachTo(this)
        SwingUtilities.invokeLater {
//            svgSkiaWidget.nativeLayer.needRedraw()
            skikoView.skiaLayer.needRedraw()
        }
    }

    override fun getPreferredSize(): Dimension {
//        val size = Dimension(svgSkiaWidget.width().toInt(), svgSkiaWidget.height().toInt())
//        svgSkiaWidget.nativeLayer.preferredSize = size
//        return size
        return skikoView.skiaLayer.preferredSize
    }

    override fun dispose() {
//        svgSkiaWidget.nativeLayer.dispose()
        skikoView.dispose()
    }
}
