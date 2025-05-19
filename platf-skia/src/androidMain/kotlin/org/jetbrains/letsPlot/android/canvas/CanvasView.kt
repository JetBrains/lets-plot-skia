/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.android.canvas

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.AnimationProvider
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import java.util.logging.Logger


private val LOG = Logger.getLogger("CanvasView")

@SuppressLint("ViewConstructor")
class CanvasView(context: Context) : RelativeLayout(context) {
    init {
        layoutParams = FrameLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    private val canvasControl: CanvasControl = AndroidCanvasControl(context)
    private var figureRegistration: Registration = Registration.EMPTY

    var figure: CanvasFigure? = null
        set(fig) {
            if (field == fig) {
                return
            }

            figureRegistration.remove()
            if (fig != null) {
                figureRegistration = fig.mapToCanvas(canvasControl)
            }
            field = fig

        }

    inner class AndroidCanvasControl(
        private val context: Context,
    ) : CanvasControl {
        override val size: Vector
            get() = TODO()

        //private val mouseEventSource: MouseEventSource = AndroidMouseEventMapper(context)
        override val pixelDensity: Double = context.resources.displayMetrics.density.toDouble()

        private val handler = Handler(Looper.getMainLooper())
        private val animationTimerPeer = AndroidAnimationTimerPeer(executor = { code -> handler.post(code) })

        private val myMappedCanvases = HashMap<Canvas, View>()

        override fun addChild(index: Int, canvas: Canvas) {
            val canvasComponent = AndroidCanvasView(canvas as AndroidCanvas, context)
            addView(canvasComponent, index)
            invalidate()
            myMappedCanvases[canvas] = canvasComponent
        }

        override fun addChild(canvas: Canvas) {
            addChild(childCount, canvas)
        }

        override fun removeChild(canvas: Canvas) {
            removeView(myMappedCanvases[canvas])
            invalidate()
            myMappedCanvases.remove(canvas)
        }

        override fun createAnimationTimer(eventHandler: AnimationProvider.AnimationEventHandler): AnimationProvider.AnimationTimer {
            return object : AnimationProvider.AnimationTimer {
                override fun start() {
                    animationTimerPeer.addHandler(::handle)
                }

                override fun stop() {
                    animationTimerPeer.removeHandler(::handle)
                }

                fun handle(millisTime: Long) {
                    if (eventHandler.onEvent(millisTime)) {
                        invalidate()
                    }
                }
            }
        }

        override fun createCanvas(size: Vector): Canvas {
            return AndroidCanvas.create(size, pixelDensity)
        }

        override fun createSnapshot(
            bytes: ByteArray,
            size: Vector
        ): Async<Canvas.Snapshot> {
            TODO("Not yet implemented")
        }

        override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> {
            TODO("Not yet implemented")
        }

        override fun addEventHandler(
            eventSpec: MouseEventSpec,
            eventHandler: EventHandler<MouseEvent>
        ): Registration {
            LOG.fine("addEventHandler $eventSpec")
            return Registration.EMPTY  //mouseEventSource.addEventHandler(eventSpec, eventHandler)
        }

        override fun <T> schedule(f: () -> T) {
            TODO("Not yet implemented")
        }
    }
}
