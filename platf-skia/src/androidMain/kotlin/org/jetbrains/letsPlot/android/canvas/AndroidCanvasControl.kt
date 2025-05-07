package org.jetbrains.letsPlot.android.canvas

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationEventHandler
import org.jetbrains.letsPlot.core.canvas.AnimationProvider.AnimationTimer
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import java.util.HashMap

class AndroidCanvasControl(
    override val size: Vector,
    private val animationTimerPeer: AndroidAnimationTimerPeer,
    private val mouseEventSource: MouseEventSource,
    private val context: Context,
    private val myPixelRatio: Double = 1.0
) : CanvasControl {

    private val myComponent = FrameLayout(context).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    }

    private val myMappedCanvases = HashMap<Canvas, View>()

    override fun addChild(index: Int, canvas: Canvas) {
        val canvasComponent = AndroidCanvasView(canvas as AndroidCanvas, context)
        myComponent.addView(canvasComponent, myComponent.childCount - index)
        myComponent.invalidate()
        myMappedCanvases[canvas] = canvasComponent
    }

    override fun addChild(canvas: Canvas) {
        addChild(myComponent.childCount, canvas)
    }

    override fun removeChild(canvas: Canvas) {
        myComponent.removeView(myMappedCanvases[canvas])
        myComponent.invalidate()
        myMappedCanvases.remove(canvas)
    }

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return object : AnimationTimer {
            override fun start() {
                animationTimerPeer.addHandler(::handle)
            }

            override fun stop() {
                animationTimerPeer.removeHandler(::handle)
            }

            fun handle(millisTime: Long) {
                if (eventHandler.onEvent(millisTime)) {
                    myComponent.invalidate()
                }
            }
        }
    }
    override fun createCanvas(size: Vector): Canvas {
        return AndroidCanvas.create(size, myPixelRatio)
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
        return mouseEventSource.addEventHandler(eventSpec, eventHandler)
    }

    override fun <T> schedule(f: () -> T) {
        TODO("Not yet implemented")
    }
}