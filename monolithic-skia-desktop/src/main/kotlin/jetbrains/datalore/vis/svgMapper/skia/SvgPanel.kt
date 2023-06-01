package jetbrains.datalore.vis.svgMapper.skia

import jetbrains.datalore.base.registration.Disposable
import org.jetbrains.letsPlot.skiko.SvgSkiaWidget
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.SwingUtilities

internal class SvgPanel(
    private val svgSkiaWidget: SvgSkiaWidget
) : JPanel(), Disposable {

    init {
        layout = GridLayout(0, 1, 0, 0)
        border = null // BorderFactory.createLineBorder(Color.ORANGE, 1)
        svgSkiaWidget.nativeLayer.attachTo(this)
        SwingUtilities.invokeLater { svgSkiaWidget.nativeLayer.needRedraw() }
    }

    override fun getPreferredSize(): Dimension {
        val size = Dimension(svgSkiaWidget.width().toInt(), svgSkiaWidget.height().toInt())
        svgSkiaWidget.nativeLayer.preferredSize = size
        return size
    }

    override fun dispose() {
        svgSkiaWidget.nativeLayer.dispose()
    }
}