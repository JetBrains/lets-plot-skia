/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.awt.view

import org.jetbrains.letsPlot.awt.util.AwtEventUtil
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.skia.view.SvgSkikoView
import org.jetbrains.skiko.SkiaLayer
import java.awt.Dimension
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener

typealias LetsPlotMouseEvent = org.jetbrains.letsPlot.commons.event.MouseEvent

internal class SvgSkikoViewAwt : SvgSkikoView() {
    override fun updateSkiaLayerSize(width: Int, height: Int) {
        skiaLayer.preferredSize = Dimension(width, height)
    }

    override fun createSkiaLayer(view: SvgSkikoView): SkiaLayer {

        return SkiaLayer().also {
            it.renderDelegate = view
            it.addMouseListener(object : MouseListener {
                override fun mouseClicked(e: MouseEvent) {
                    view.eventDispatcher?.dispatchMouseEvent(MouseEventSpec.MOUSE_CLICKED, AwtEventUtil.translate(e))
                }

                override fun mousePressed(e: MouseEvent) {
                    view.eventDispatcher?.dispatchMouseEvent(MouseEventSpec.MOUSE_PRESSED, AwtEventUtil.translate(e))
                }

                override fun mouseReleased(e: MouseEvent) {
                    view.eventDispatcher?.dispatchMouseEvent(MouseEventSpec.MOUSE_RELEASED, AwtEventUtil.translate(e))
                }

                override fun mouseEntered(e: MouseEvent) {
                    view.eventDispatcher?.dispatchMouseEvent(MouseEventSpec.MOUSE_ENTERED, AwtEventUtil.translate(e))
                }

                override fun mouseExited(e: MouseEvent) {
                    view.eventDispatcher?.dispatchMouseEvent(MouseEventSpec.MOUSE_LEFT, AwtEventUtil.translate(e))
                }
            })

            it.addMouseMotionListener(object : MouseMotionListener {
                override fun mouseDragged(e: MouseEvent) {
                    view.eventDispatcher?.dispatchMouseEvent(MouseEventSpec.MOUSE_DRAGGED, AwtEventUtil.translate(e))
                }

                override fun mouseMoved(e: MouseEvent) {
                    view.eventDispatcher?.dispatchMouseEvent(MouseEventSpec.MOUSE_MOVED, AwtEventUtil.translate(e))
                }
            })
        }
    }
}