/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.awt.view

import org.jetbrains.letsPlot.awt.util.AwtEventUtil.translate
import org.jetbrains.letsPlot.commons.event.MouseEventSpec.*
import org.jetbrains.letsPlot.skia.view.SvgSkikoView
import org.jetbrains.skiko.SkiaLayer
import java.awt.Desktop
import java.awt.Dimension
import java.awt.event.*


internal class SvgSkikoViewAwt : SvgSkikoView() {
    override fun updateSkiaLayerSize(width: Int, height: Int) {
        skiaLayer.preferredSize = Dimension(width, height)
    }

    override fun createSkiaLayer(view: SvgSkikoView): SkiaLayer {
        return SkiaLayer().also {
            it.renderDelegate = view
            it.addMouseListener(object : MouseListener {
                override fun mouseClicked(e: MouseEvent) { onMouseEvent(MOUSE_CLICKED, translate(e)) }
                override fun mousePressed(e: MouseEvent) { onMouseEvent(MOUSE_PRESSED, translate(e)) }
                override fun mouseReleased(e: MouseEvent) { onMouseEvent(MOUSE_RELEASED, translate(e)) }
                override fun mouseEntered(e: MouseEvent) { onMouseEvent(MOUSE_ENTERED, translate(e)) }
                override fun mouseExited(e: MouseEvent) { onMouseEvent(MOUSE_LEFT, translate(e)) }
            })
            it.addMouseMotionListener(object : MouseMotionListener {
                override fun mouseDragged(e: MouseEvent) { onMouseEvent(MOUSE_DRAGGED, translate(e)) }
                override fun mouseMoved(e: MouseEvent) { onMouseEvent(MOUSE_MOVED, translate(e)) }
            })
            it.addMouseWheelListener(object : MouseWheelListener {
                override fun mouseWheelMoved(e: MouseWheelEvent) {  onMouseEvent(MOUSE_WHEEL_ROTATED, translate(e)) }
            })
        }
    }

    override fun onHrefClick(href: String) {
        Desktop.getDesktop().browse(java.net.URI(href))
    }
}