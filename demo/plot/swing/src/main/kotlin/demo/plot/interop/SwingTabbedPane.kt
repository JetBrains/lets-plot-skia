/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.letsPlot.composeDesktop.minimal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.geom.geomDensity
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import org.jetbrains.letsPlot.themes.theme
import org.jetbrains.letsPlot.themes.elementRect
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.JTabbedPane
import javax.swing.JTextArea
import javax.swing.JScrollPane

/**
 * Entry point that demonstrates embedding Lets-Plot PlotPanel into a Swing JFrame via ComposePanel (ComposePane).
 */
fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame("Lets-Plot in Swing via ComposePanel").apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            layout = BorderLayout()
            setSize(800, 600)
            setLocationRelativeTo(null)
            contentPane.background = java.awt.Color(0x12, 0x12, 0x12)
        }

        val composePanel = ComposePanel().apply {
            background = java.awt.Color(0x12, 0x12, 0x12)
            setContent {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF121212))
                        .border(1.dp, color = Color(0xFF2F2F2F))
                ) {
                    // Left pane - takes 50% of available width (equivalent to 0.5f split fraction)
                    BasicText(
                        text = "compose text",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        style = TextStyle(color = Color.White)
                    )
                    
                    // Right pane - takes remaining 50% of available width
                    PlotPanel(
                        figure = createFigure(),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) { computationMessages ->
                        computationMessages.forEach { println("[DEMO APP MESSAGE] $it") }
                    }
                }
            }
        }

        val tabs = JTabbedPane().apply {
            background = java.awt.Color.BLUE
            foreground = java.awt.Color.WHITE
            addTab("Plot", composePanel)
            addTab("Hello", JScrollPane(JTextArea("Swing Text Area")).apply { border = null })
        }

        frame.add(tabs, BorderLayout.CENTER)
        frame.isVisible = true
    }
}

private fun createFigure(): Figure {
    val rand = java.util.Random()
    val n = 200
    val xs = List(n) { rand.nextGaussian() }
    val data = mapOf<String, Any>(
        "x" to xs
    )

    return letsPlot(data) + geomDensity { x = "x" } + theme(
        plotBackground = elementRect(fill = "#121212", color = "#121212"),
        panelBackground = elementRect(fill = "#121212", color = "#121212")
    )
}
