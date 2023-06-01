package demo.svgMapping.utils

import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.skia.svgComponent
import java.awt.Color
import java.awt.GridLayout
import javax.swing.*
import kotlin.math.min

class DemoWindow(
    title: String,
    private val svgRoots: List<SvgSvgElement>,
    private val maxCol: Int = 2,
) : JFrame("$title (Skia Swing)") {
    private val rootPanel: JPanel

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        rootPanel = JPanel()
        rootPanel.layout = GridLayout(0, min(maxCol, svgRoots.size))
//        rootPanel.background = Color.WHITE
        rootPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)


        // Fixed plot size
        val scrollPane = JScrollPane(
            rootPanel,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED

        )
        contentPane.add(scrollPane)
    }

    fun open() {
        SwingUtilities.invokeLater {
            createWindowContent()

            pack()
            setLocationRelativeTo(null)  // move to the screen center
            isVisible = true
        }
    }

    private fun createWindowContent() {
        for (svgRoot in svgRoots) {
            rootPanel.add(createPlotComponent(svgRoot))
        }
    }

    private fun createPlotComponent(svgRoot: SvgSvgElement): JComponent {
        val component = svgComponent(svgRoot)
        component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)
        return component
    }
}