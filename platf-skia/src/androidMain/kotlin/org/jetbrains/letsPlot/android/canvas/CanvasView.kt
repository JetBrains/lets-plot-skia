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
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.property.PropertyChangeEvent
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.AnimationProvider
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import java.util.*

val CanvasFigure.width get() = bounds().get().dimension.x
val CanvasFigure.height get() = bounds().get().dimension.y

@SuppressLint("ViewConstructor")
class CanvasView(context: Context) : View(context) {
    var figure: CanvasFigure? = null
        set(fig) {
            if (field == fig) {
                return
            }

            field = fig

            figureRegistration.remove()
            if (fig != null) {
                figureRegistration = Registration.from(
                    fig.mapToCanvas(canvasControl),
                    fig.bounds().addHandler(object : EventHandler<PropertyChangeEvent<out Rectangle>> {
                        override fun onEvent(event: PropertyChangeEvent<out Rectangle>) {
                            requestLayout()
                            invalidate()
                        }
                    })
                )
            }

            requestLayout()
            invalidate()
        }

    private val looper = Handler(Looper.getMainLooper())
    private val animationTimer: Timer = Timer()
    private val animationUpdateRate: Int = 60
    private val animationTimerHandlers = ArrayList<(Long) -> Unit>()
    private val animationTimerTask = object : TimerTask() {
        override fun run() {
            looper.post {
                synchronized(animationTimerHandlers) {
                    animationTimerHandlers.forEach {
                        it(System.currentTimeMillis())
                    }
                }
            }
        }
    }

    private val canvasControl = AndroidCanvasControl()
    private var figureRegistration: Registration = Registration.EMPTY
    private val sizeListeners = mutableListOf<(Vector) -> Unit>()
    private val mouseEventSource: MouseEventSource = AndroidMouseEventMapper(this, context)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val density = resources.displayMetrics.density
        val newSize = Vector((w / density).toInt(), (h / density).toInt())

        sizeListeners.forEach { it(newSize) }
    }

    override fun onDraw(canvas: android.graphics.Canvas) {
        super.onDraw(canvas)

        val contentCanvas = canvasControl.children.lastOrNull() ?: return

        if (contentCanvas.size.x <= 0 || contentCanvas.size.y <= 0) {
            // No content to draw, skip drawing
            return
        }

        canvas.drawBitmap(contentCanvas.platformBitmap, 0f, 0f, null)
        //canvasControl.children.forEach {
        //    canvas.drawBitmap(it.platformBitmap, 0f, 0f, null)
        //}
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animationTimer.schedule(animationTimerTask, 0L, 1000L / animationUpdateRate)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        figureRegistration.remove()
        canvasControl.children.clear()
        animationTimer.cancel()
        synchronized(animationTimerHandlers) {
            animationTimerHandlers.clear()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val density = resources.displayMetrics.density

        val figureWidthInDp = figure?.width ?: 0
        val figureHeightInDp = figure?.height ?: 0

        val desiredWidth = (figureWidthInDp * density).toInt()
        val desiredHeight = (figureHeightInDp * density).toInt()

        val finalWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec)
            MeasureSpec.AT_MOST -> minOf(desiredWidth, MeasureSpec.getSize(widthMeasureSpec))
            else -> desiredWidth
        }

        val finalHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
            MeasureSpec.AT_MOST -> minOf(desiredHeight, MeasureSpec.getSize(heightMeasureSpec))
            else -> desiredHeight
        }

        setMeasuredDimension(finalWidth, finalHeight)
    }


    inner class AndroidCanvasControl : CanvasControl {
        val children = mutableListOf<AndroidCanvas>()

        override fun createCanvas(size: Vector) = AndroidCanvas.create(size, pixelDensity)
        override fun createSnapshot(bitmap: Bitmap) = AndroidSnapshot.fromBitmap(bitmap)
        override fun snapshot() = error("Snapshot not supported in AndroidCanvasControl")
        override fun decodeDataImageUrl(dataUrl: String) = error("decodeDataImageUrl not supported in AndroidCanvasControl")
        override fun decodePng(png: ByteArray, size: Vector) = error("decodePng not supported in AndroidCanvasControl")
        override fun <T> schedule(f: () -> T) = error("schedule not supported in AndroidCanvasControl")

        override val size: Vector
            get() = Vector(
                (this@CanvasView.width / pixelDensity).toInt(),
                (this@CanvasView.height / pixelDensity).toInt()
            )

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

        override fun addEventHandler(
            eventSpec: MouseEventSpec,
            eventHandler: EventHandler<MouseEvent>
        ): Registration {
            return mouseEventSource.addEventHandler(eventSpec, eventHandler)
        }

        override fun createAnimationTimer(eventHandler: AnimationProvider.AnimationEventHandler): AnimationProvider.AnimationTimer {
            // Timer is already running and invokes the `handle` method on animationTimerHandlers
            // Add the handler to the list to "start" it and remove it to "stop" it.
            return object : AnimationProvider.AnimationTimer {
                override fun start() {
                    synchronized(animationTimerHandlers) {
                        animationTimerHandlers.add(::handle)
                    }
                }

                override fun stop() {
                    synchronized(animationTimerHandlers) {
                        animationTimerHandlers.remove(::handle)
                    }
                }

                fun handle(millisTime: Long) {
                    if (eventHandler.onEvent(millisTime)) {
                        invalidate()
                    }
                }
            }
        }
    }
}
