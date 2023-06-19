package org.jetbrains.letsPlot.skia.svg.view

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svg.SvgSvgElement
import org.jetbrains.letsPlot.skia.svg.mapper.SvgSkiaPeer
import org.jetbrains.letsPlot.skia.svg.mapper.SvgSvgElementMapper
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Drawable
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoGestureEvent
import org.jetbrains.skiko.SkikoPointerEvent
import org.jetbrains.skiko.SkikoView
import kotlin.math.ceil

abstract class SvgSkikoView constructor(
    svg: SvgSvgElement,
    eventDispatcher: SkikoViewEventDispatcher?
) : SkikoView, Disposable {

    private val nodeContainer = SvgNodeContainer(svg)  // attach root
    private val drawable: Drawable
    private lateinit var _nativeLayer: SkiaLayer

    private var disposed = false

    val skiaLayer: SkiaLayer
        get() {
            check(!disposed) { "SvgSkikoView is disposed." }
            if (!this::_nativeLayer.isInitialized) {
                _nativeLayer = createSkiaLayer(this)
            }
            return _nativeLayer
        }

    val width: Int = svg.width().get()?.let { ceil(it).toInt() } ?: 0
    val height: Int = svg.height().get()?.let { ceil(it).toInt() } ?: 0

    val eventDispatcher: SkikoViewEventDispatcher? by lazy {
        eventDispatcher?.let { externalDispatcher ->
            object : SkikoViewEventDispatcher {
                override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
                    if (!disposed) {
                        externalDispatcher.dispatchMouseEvent(kind, e)
                        skiaLayer.needRedraw()
                    }
                }
            }
        }
    }

    init {
        val rootMapper = SvgSvgElementMapper(svg, SvgSkiaPeer())
        rootMapper.attachRoot(MappingContext())
        drawable = rootMapper.target.drawable
    }

    protected abstract fun createSkiaLayer(view: SvgSkikoView): SkiaLayer

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        canvas.scale(skiaLayer.contentScale, skiaLayer.contentScale)
        canvas.drawDrawable(drawable)
    }

    override fun onGestureEvent(event: SkikoGestureEvent) {
        eventDispatcher?.let { dispatcher ->
            event.translate()?.let {
                dispatcher.dispatchMouseEvent(kind = it.first, e = it.second)
            }
        }
    }

    override fun onPointerEvent(event: SkikoPointerEvent) {
        eventDispatcher?.let { dispatcher ->
            event.translate()?.let {
                dispatcher.dispatchMouseEvent(kind = it.first, e = it.second)
            }
        }
    }

    override fun dispose() {
        disposed = true

        // Detach svg root.
        nodeContainer.root().set(SvgSvgElement())

        if (this::_nativeLayer.isInitialized) {
            _nativeLayer.detach()
        }
    }
}