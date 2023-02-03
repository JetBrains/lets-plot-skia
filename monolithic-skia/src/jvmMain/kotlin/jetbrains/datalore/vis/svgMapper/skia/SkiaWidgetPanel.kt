package jetbrains.datalore.vis.svgMapper.skia

import jetbrains.datalore.base.registration.Disposable
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.SwingUtilities

internal class SkiaWidgetPanel(
    private val skiaWidget: SkiaWidget
) : JPanel(), Disposable {

    init {
        layout = GridLayout(0, 1, 5, 5)
        border = BorderFactory.createLineBorder(Color.ORANGE, 1)
        skiaWidget.nativeLayer.attachTo(this)
        SwingUtilities.invokeLater { skiaWidget.nativeLayer.needRedraw() }
    }

    override fun getPreferredSize(): Dimension {
        val size = Dimension(skiaWidget.width().toInt(), skiaWidget.height().toInt())
        skiaWidget.nativeLayer.preferredSize = size
        return size
    }

    override fun dispose() {
        skiaWidget.nativeLayer.dispose()
    }
}
