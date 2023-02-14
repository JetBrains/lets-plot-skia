package me.ikupriyanov.demo.utils

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.svgMapper.skia.plotComponent
import java.awt.Component
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import javax.swing.*
import kotlin.math.min

class PlotWindowSkia(
    title: String,
    private val specList: List<MutableMap<String, Any>>,
    maxCol: Int = 3,
    private val plotSize: DoubleVector? = null
) : JFrame("$title (Skia SWING)") {
    private val rootPanel: JPanel

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        rootPanel = JPanel()
        rootPanel.layout = GridLayout(0, min(maxCol, specList.size))
        rootPanel.background = background
        rootPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        addWindowListener(object : WindowListener {
            override fun windowActivated(e: WindowEvent?) {
                repaint()
            }

            override fun windowOpened(e: WindowEvent?) {}
            override fun windowClosing(e: WindowEvent?) {}
            override fun windowClosed(e: WindowEvent?) {}
            override fun windowIconified(e: WindowEvent?) {}
            override fun windowDeiconified(e: WindowEvent?) {}
            override fun windowDeactivated(e: WindowEvent?) {}
        })

        if (plotSize == null) {
            contentPane.add(rootPanel)
        } else {
            // Fixed plot size
            val scrollPane = JScrollPane(
                rootPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED

            )
            contentPane.add(scrollPane)
        }
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
        for (spec in specList) {
            rootPanel.add(createPlotComponent(spec, plotSize))
        }
    }


    private fun createPlotComponent(rawSpec: MutableMap<String, Any>, plotSize: DoubleVector?): JComponent {
        val plotPanel = plotComponent(
            plotSpec = rawSpec,
            plotSize
        )

        plotSize?.let {
            plotPanel.preferredSize = Dimension(it.x.toInt(), it.y.toInt())
        }

        plotPanel.alignmentX = Component.CENTER_ALIGNMENT
        return plotPanel
    }
}