/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svg.utils

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.skia.awt.view.SvgPanel
import java.awt.Color
import java.awt.GridLayout
import javax.swing.*
import kotlin.math.min

internal class DemoWindow(
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
            rootPanel.add(createSvgPanel(svgRoot))
        }
    }

    private fun createSvgPanel(svgRoot: SvgSvgElement): JComponent {
        val component = SvgPanel(svgRoot)
        component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)
        return component
    }
}