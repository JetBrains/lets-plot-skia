/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.util

import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.awt.canvas.AwtAnimationTimerPeer
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasControl
import org.jetbrains.letsPlot.awt.canvas.AwtMouseEventMapper
import org.jetbrains.letsPlot.awt.util.AwtEventUtil.translate
import org.jetbrains.letsPlot.commons.event.MouseEventSpec.*
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.raster.view.SvgCanvasView
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.Rectangle
import java.awt.event.*
import javax.swing.*
import kotlin.math.min

internal class PlotSpecsCanvasDemoWindow(
    title: String,
    private val figures: List<Figure>,
    maxCol: Int = 3,
    private val plotSize: Dimension? = null,
    background: Color = Color.WHITE,
) : JFrame("$title (Swing Canvas)") {
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
            size = Dimension(600, 600)
        }
    }

    private fun createWindowContent() {
        val preferredSizeFromPlot = (plotSize == null)
        val components = figures.map { figure ->
            val figureComponent = figure.createCanvas(
                preferredSizeFromPlot = preferredSizeFromPlot
            ) { messages ->
                for (message in messages) {
                    println("[Demo Plot Viewer] $message")
                }
            }

            plotSize?.let {
                figureComponent.preferredSize = it
            }

            figureComponent.border = BorderFactory.createLineBorder(Color.ORANGE, 1)
            figureComponent
        }

        components.forEach { rootPanel.add(it) }
    }
}

class SwingSvgCanvasView : SvgCanvasView() {
    val container = JPanel(null)
    private var awtCanvasControl: AwtCanvasControl? = null

    override fun createCanvasControl(view: SvgCanvasView): CanvasControl {
        if (awtCanvasControl != null) return awtCanvasControl!!
        val w = 600
        val h = 400
        val awtCanvasControl = AwtCanvasControl(
            size = Vector(w, h),
            animationTimerPeer = AwtAnimationTimerPeer(),
            mouseEventSource = AwtMouseEventMapper(container)
        )
        val canvasComponent = awtCanvasControl.component()
        canvasComponent.bounds = Rectangle(0, 0, w, h)
        canvasComponent.background = Color.WHITE
        canvasComponent.border = BorderFactory.createLineBorder(Color.RED, 5)
        container.add(canvasComponent)
        container.bounds = Rectangle(0, 0, w, h)
        this.awtCanvasControl = awtCanvasControl

        canvasComponent.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
                val event = when (e.clickCount) {
                    1 -> MOUSE_CLICKED
                    2 -> MOUSE_DOUBLE_CLICKED
                    else -> return
                }

                onMouseEvent(event, translate(e))
            }
            override fun mousePressed(e: MouseEvent) { onMouseEvent(MOUSE_PRESSED, translate(e)) }
            override fun mouseReleased(e: MouseEvent) { onMouseEvent(MOUSE_RELEASED, translate(e)) }
            override fun mouseEntered(e: MouseEvent) { onMouseEvent(MOUSE_ENTERED, translate(e)) }
            override fun mouseExited(e: MouseEvent) { onMouseEvent(MOUSE_LEFT, translate(e)) }
        })
        canvasComponent.addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent) { onMouseEvent(MOUSE_DRAGGED, translate(e)) }
            override fun mouseMoved(e: MouseEvent) { onMouseEvent(MOUSE_MOVED, translate(e)) }
        })
        canvasComponent.addMouseWheelListener(object : MouseWheelListener {
            override fun mouseWheelMoved(e: MouseWheelEvent) {  onMouseEvent(MOUSE_WHEEL_ROTATED, translate(e)) }
        })


        return awtCanvasControl
    }

    override fun updateCanvasSize(width: Int, height: Int) {
        println("updateCanvasSize: $width x $height")
    }

    override fun onHrefClick(href: String) {
        TODO("Not yet implemented")
    }

}