/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping.utils

import org.jetbrains.letsPlot.awt.canvas.AwtAnimationTimerPeer
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasControl
import org.jetbrains.letsPlot.awt.canvas.AwtMouseEventMapper
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.raster.SvgCanvasFigure
import java.awt.Color
import java.awt.GridLayout
import javax.swing.*
import kotlin.math.min

internal class CanvasDemoWindow(
    title: String,
    private val svgRoots: List<SvgSvgElement>,
    private val maxCol: Int = 2,
) : JFrame("$title (Swing Canvas)") {
    private val rootPanel: JPanel

    init {
        defaultCloseOperation = EXIT_ON_CLOSE

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
        val panel = JPanel(null)
        val dim = Vector(
            svgRoot.width().get()?.toInt() ?: 800,
            svgRoot.height().get()?.toInt() ?: 600
        )

        val canvasControl = AwtCanvasControl(
            dim,
            animationTimerPeer = AwtAnimationTimerPeer(),
            mouseEventSource = AwtMouseEventMapper(panel)

        )

        val component = canvasControl.component()
        component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)

        //val rootMapper = SvgSvgElementMapper(svgDocument, SvgSkiaPeer(fontManager))
        SvgCanvasFigure(svgRoot).mapToCanvas(canvasControl)

        return component
    }
}