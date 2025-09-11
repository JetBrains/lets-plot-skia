/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.util

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import org.jetbrains.letsPlot.skia.compose.PlotPanelRaw
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import kotlin.math.min
import java.awt.Color as ColorAwt

internal class PlotSpecsDemoWindow(
    title: String,
    private val figures: List<Any>, // Element can be Figure or MutableMap<String, Any> (raw plot spec)
    maxCol: Int = 3,
    private val plotSize: Dimension? = null,
    background: ColorAwt = ColorAwt.WHITE,
) : JFrame("$title (Skia Swing)") {
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

        figures.forEach { figure ->
            val composePanel = ComposePanel().apply {
                if (plotSize != null) {
                    preferredSize = plotSize
                } else {
                    preferredSize = Dimension(500, 400) // Default size
                }

                setContent {
                    when (figure) {
                        is Figure -> {
                            PlotPanel(
                                figure = figure,
                                preserveAspectRatio = true,
                                modifier = Modifier.fillMaxSize(),
                                computationMessagesHandler = { messages ->
                                    for (message in messages) {
                                        println("[Demo Plot Viewer] $message")
                                    }
                                }
                            )
                        }

                        is Map<*, *> -> {
                            @Suppress("UNCHECKED_CAST")
                            PlotPanelRaw(
                                rawSpec = figure as MutableMap<String, Any>,
                                preserveAspectRatio = true,
                                modifier = Modifier.fillMaxSize(),
                                computationMessagesHandler = { messages ->
                                    for (message in messages) {
                                        println("[Demo Plot Viewer] $message")
                                    }
                                }
                            )
                        }

                        else -> error("Unexpected figure type: ${figure::class}")
                    }
                }
            }

            rootPanel.add(composePanel)
        }
    }
}