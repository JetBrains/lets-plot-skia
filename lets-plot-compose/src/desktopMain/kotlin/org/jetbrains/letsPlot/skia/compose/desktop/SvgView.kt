/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose.desktop

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import org.jetbrains.letsPlot.commons.event.KeyModifiers
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec.*
import org.jetbrains.letsPlot.commons.event.MouseWheelEvent
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.skia.view.SkiaSvgView
import java.awt.Desktop
import kotlin.math.roundToInt


@OptIn(ExperimentalComposeUiApi::class)
internal class SvgView : SkiaSvgView() {

    // Callback to notify Compose when a redrawing is needed
    var onRedrawRequested: (() -> Unit)? = null

    // Position offset for centering the plot
    private var offsetX: Float = 0f
    private var offsetY: Float = 0f

    fun setPosition(x: Float, y: Float) {
        offsetX = x
        offsetY = y
        needRedraw()
    }

    override fun needRedraw() {
        // Forward redraw requests to the Compose recomposition mechanism.
        onRedrawRequested?.invoke()
    }

    override fun onHrefClick(href: String) {
        Desktop.getDesktop().browse(java.net.URI(href))
    }

    fun render(drawScope: DrawScope) {
        @Suppress("USELESS_CAST")
        val canvas = drawScope.drawContext.canvas.nativeCanvas as org.jetbrains.skia.Canvas

        // Apply position offset for centering
        canvas.save()
        try {
            canvas.translate(offsetX, offsetY)
            renderIntern(canvas)
        } finally {
            canvas.restore()
        }
    }

    fun handlePointerEvent(pointerEvent: PointerEvent): Boolean {
        val change = pointerEvent.changes.first()
        val position = change.position

        // Adjust coordinates to account for the position offset
        val adjustedX = (position.x - offsetX).roundToInt()
        val adjustedY = (position.y - offsetY).roundToInt()
        val vector = Vector(adjustedX, adjustedY)

        // Translate PointerEvent to lets-plot MouseEvent
        val mouseEvent = when {
            change.pressed -> MouseEvent.leftButton(vector)
            else -> MouseEvent.noButton(vector)
        }

        when (pointerEvent.type) {
            PointerEventType.Press -> {
                onMouseEvent(MOUSE_PRESSED, mouseEvent)
                return true
            }

            PointerEventType.Release -> {
                onMouseEvent(MOUSE_RELEASED, mouseEvent)
                return true
            }

            PointerEventType.Move -> {
                if (change.pressed) {
                    onMouseEvent(MOUSE_DRAGGED, mouseEvent)
                } else {
                    onMouseEvent(MOUSE_MOVED, mouseEvent)
                }
                return true
            }

            PointerEventType.Enter -> {
                onMouseEvent(MOUSE_ENTERED, mouseEvent)
                return true
            }

            PointerEventType.Exit -> {
                onMouseEvent(MOUSE_LEFT, mouseEvent)
                return true
            }

            PointerEventType.Scroll -> {
                val scrollDelta = change.scrollDelta
                val wheelMouseEvent = MouseWheelEvent(
                    x = vector.x,
                    y = vector.y,
                    button = org.jetbrains.letsPlot.commons.event.Button.NONE,
                    modifiers = KeyModifiers.emptyModifiers(),
                    scrollAmount = scrollDelta.y.toDouble()
                )
                onMouseEvent(MOUSE_WHEEL_ROTATED, wheelMouseEvent)
                return true
            }
        }

        return false
    }

    fun handleClick(position: Offset, clickCount: Int) {
        // Adjust coordinates to account for the position offset
        val adjustedX = (position.x - offsetX).roundToInt()
        val adjustedY = (position.y - offsetY).roundToInt()
        val vector = Vector(adjustedX, adjustedY)
        val mouseEvent = MouseEvent.leftButton(vector)

        val eventSpec = when (clickCount) {
            1 -> MOUSE_CLICKED
            2 -> MOUSE_DOUBLE_CLICKED
            else -> return
        }

        onMouseEvent(eventSpec, mouseEvent)
    }
}