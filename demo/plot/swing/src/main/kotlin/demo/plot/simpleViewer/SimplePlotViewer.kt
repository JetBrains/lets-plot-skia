/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.simpleViewer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.compose.PlotPanel
import java.awt.Dimension
import javax.swing.SwingUtilities

class SimplePlotViewer(
    private val title: String,
    private val figure: Figure,
    private val windowSize: Dimension? = null,
    private val preserveAspectRatio: Boolean = false
) {

    fun open() {
        SwingUtilities.invokeLater {
            val window = ComposeWindow()

            window.title = title
            windowSize?.let { window.size = it } ?: run {
                window.size = Dimension(800, 600)
            }

            window.setContent {
                PlotPanel(
                    figure = figure,
                    preserveAspectRatio = preserveAspectRatio,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    computationMessagesHandler = { messages ->
                        // Print computation messages to console
                        messages.forEach { println("Plot computation: $it") }
                    }
                )
            }

            window.setLocationRelativeTo(null) // Center on screen
            window.isVisible = true
        }
    }
}