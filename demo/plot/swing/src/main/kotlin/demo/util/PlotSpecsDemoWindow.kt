/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.util

import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.skia.swing.createComponent
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import kotlin.math.min

internal class PlotSpecsDemoWindow(
    title: String,
    private val figures: List<Figure>,
    maxCol: Int = 3,
    private val plotSize: Dimension? = null,
    background: Color = Color.WHITE,
) : JFrame(title) {
    private val rootPanel: JPanel

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        rootPanel = JPanel()
        rootPanel.layout = GridLayout(0, min(maxCol, figures.size))
        rootPanel.background = background
        rootPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

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
        val preferredSizeFromPlot = (plotSize == null)
        val components = figures.map { figure ->
            val figureComponent = figure.createComponent(
                preferredSizeFromPlot = preferredSizeFromPlot
            ) { messages ->
                for (message in messages) {
                    println("[Demo Plot Viewer] $message")
                }
            }

            plotSize?.let {
                figureComponent.preferredSize = it
            }

            figureComponent
        }

        components.forEach { rootPanel.add(it) }
    }
}