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
class CanvasView(context: Context) : View(context) {
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

    private val canvasControl = AndroidCanvasControl(context)
    private var figureRegistration: Registration = Registration.EMPTY
    private val sizeListeners = mutableListOf<(Vector) -> Unit>()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val density = resources.displayMetrics.density
        val newSize = Vector((w / density).toInt(), (h / density).toInt())
        sizeListeners.forEach { it(newSize) }
    }

    override fun onDraw(canvas: android.graphics.Canvas) {
        super.onDraw(canvas)

        canvasControl.children.forEach {
            canvas.drawBitmap(it.bitmap, 0f, 0f, null)
        }
    }

    //override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    //    val density = resources.displayMetrics.density
    //    val width = 2000
    //    val height = 2000
//
    //    println("onMeasure: $width x $height, density: $density")
//
    //    measureChild(
    //        getChildAt(0),
    //        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
    //        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
    //    )
//
    //    setMeasuredDimension(width, height)
    //}

    inner class AndroidCanvasControl(
        context: Context,
    ) : CanvasControl {
        private val handler = Handler(Looper.getMainLooper())
        private val animationTimerPeer = AndroidAnimationTimerPeer(executor = { code -> handler.post(code) })
        val children = mutableListOf<AndroidCanvas>()

        override fun createCanvas(size: Vector): Canvas {
            return AndroidCanvas.create(size, pixelDensity)
        }

        override val size: Vector
            get() = Vector(
                (this@CanvasView.width / pixelDensity).toInt(),
                (this@CanvasView.height / pixelDensity).toInt()
            )

        //private val mouseEventSource: MouseEventSource = AndroidMouseEventMapper(context)
        override val pixelDensity: Double = context.resources.displayMetrics.density.toDouble()

        override fun addChild(index: Int, canvas: Canvas) {
            children.add(canvas as AndroidCanvas)
            invalidate()
        }

        override fun addChild(canvas: Canvas) {
            addChild(children.size, canvas)
        }

        override fun removeChild(canvas: Canvas) {
            children.remove(canvas)
            invalidate()
        }

        override fun onResize(listener: (Vector) -> Unit): Registration {
            sizeListeners.add(listener)
            return object : Registration() {
                override fun doRemove() {
                    sizeListeners.remove(listener)
                }
            }
        }

        override fun snapshot(): Canvas.Snapshot {
            TODO("Not yet implemented")
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

        override fun createAnimationTimer(eventHandler: AnimationProvider.AnimationEventHandler): AnimationProvider.AnimationTimer {
            return object : AnimationProvider.AnimationTimer {
                override fun start() = animationTimerPeer.addHandler(::handle)
                override fun stop() = animationTimerPeer.removeHandler(::handle)

                fun handle(millisTime: Long) {
                    if (eventHandler.onEvent(millisTime)) {
                        invalidate()
                    }
                }
            }
        }
    }
}
