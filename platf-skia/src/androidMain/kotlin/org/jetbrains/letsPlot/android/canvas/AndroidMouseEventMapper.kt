/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.android.canvas


import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration

class AndroidMouseEventMapper(
    eventSource: View,
    private val context: Context,
) : MouseEventSource {
    private val mouseEventPeer = MouseEventPeer()

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return mouseEventPeer.addEventHandler(eventSpec, eventHandler)
    }

    init {
        eventSource.setOnTouchListener { _, event ->
            onTouchEvent(event)
        }
    }

    private val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            val coord = translateMouseEvent(e)
            mouseEventPeer.dispatch(MouseEventSpec.MOUSE_PRESSED, MouseEvent.leftButton(coord))
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val coord = translateMouseEvent(e)
            mouseEventPeer.dispatch(MouseEventSpec.MOUSE_MOVED, MouseEvent.noButton(coord)) // to show tooltip
            mouseEventPeer.dispatch(MouseEventSpec.MOUSE_CLICKED, MouseEvent.leftButton(coord))
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            val coord = translateMouseEvent(e)
            mouseEventPeer.dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, MouseEvent.leftButton(coord))
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            val coord = translateMouseEvent(e2)
            mouseEventPeer.dispatch(MouseEventSpec.MOUSE_MOVED, MouseEvent.leftButton(coord))
            return true
        }
    })

    fun translateMouseEvent(e: MotionEvent): Vector {
        val density = context.resources.displayMetrics.density
        val v = Vector((e.x / density).toInt(), (e.y / density).toInt())
        return v
    }

    private fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

}
