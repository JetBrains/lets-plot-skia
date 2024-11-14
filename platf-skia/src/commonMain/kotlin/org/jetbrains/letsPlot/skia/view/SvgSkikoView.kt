/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.view

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.skia.mapping.svg.DebugOptions
import org.jetbrains.letsPlot.skia.mapping.svg.DebugOptions.drawBoundingBoxes
import org.jetbrains.letsPlot.skia.mapping.svg.FontManager
import org.jetbrains.letsPlot.skia.mapping.svg.SvgSkiaPeer
import org.jetbrains.letsPlot.skia.mapping.svg.SvgSvgElementMapper
import org.jetbrains.letsPlot.skia.shape.*
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Matrix33.Companion.makeScale
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PathEffect.Companion.makeDash
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoRenderDelegate
import kotlin.math.ceil

abstract class SvgSkikoView() : SkikoRenderDelegate, Disposable {
    private var eventReg: Registration = Registration.EMPTY
    private var clickedElement: Element? = null

    var eventDispatcher: SkikoViewEventDispatcher? = null
        set(value) {
            eventReg.remove()
            if (value != null) {
                eventReg = value.addEventHandler(MouseEventSpec.MOUSE_CLICKED, object : EventHandler<MouseEvent> {
                    override fun onEvent(event: MouseEvent) {
                        onMouseClicked(event)
                    }
                })
            }
            field = value
        }

    private val fontManager = FontManager()

    fun onMouseClicked(e: MouseEvent) {
        //eventDispatcher?.dispatchMouseEvent(MouseEventSpec.MOUSE_CLICKED, e)
        reversedDepthFirstTraversal(rootElement)
            .filter { it !is Path }
            .filter { it !is Group }
            .firstOrNull() { it.screenBounds.contains(e.x, e.y) }
            ?.let {
                clickedElement = if (clickedElement == it) null else it
                needRedraw()
            }
    }

    var svg: SvgSvgElement = SvgSvgElement()
        set(value) {
            nodeContainer.root().set(value)
            val rootMapper = SvgSvgElementMapper(value, SvgSkiaPeer(fontManager))
            rootMapper.attachRoot(MappingContext())
            rootElement = rootMapper.target

            width = value.width().get()?.let { ceil(it).toInt() } ?: 0
            height = value.height().get()?.let { ceil(it).toInt() } ?: 0

            updateSkiaLayerSize(width, height)

            needRedraw()
        }

    private val nodeContainer = SvgNodeContainer(SvgSvgElement())  // attach root
    private var rootElement: Pane = Pane()
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

    var width: Int = 0
        private set
    var height: Int = 0
        private set

    init {
        nodeContainer.addListener(object : SvgNodeContainerListener {
            override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) = needRedraw()
            override fun onNodeAttached(node: SvgNode) = needRedraw()
            override fun onNodeDetached(node: SvgNode) = needRedraw()
        })
    }

    protected abstract fun createSkiaLayer(view: SvgSkikoView): SkiaLayer
    protected abstract fun updateSkiaLayerSize(width: Int, height: Int)

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        if (disposed) {
            // needRedraw() schedules render call, but SvgSkikoView may be disposed before it happens between frames.
            return
        }

        if (width == 0 && height == 0) {
            // Skiko may call onRender before SkiaLayer is initialized (width and height are 0).
            // In this case we request another render call until SkiaLayer is initialized (width and height are not 0).
            // Otherwise, Skiko won't call onRender again when initialization is done and screen will stay blank.
            needRedraw()
            return
        }

        canvas.concat(makeScale(skiaLayer.contentScale))

        render(rootElement, canvas)

        highlightElement(clickedElement, canvas)

        if (DebugOptions.DEBUG_DRAWING_ENABLED) {
            drawBoundingBoxes(rootElement, canvas)
        }
    }

    override fun dispose() {
        if (disposed) {
            return
        }

        disposed = true

        fontManager.dispose()

        // Detach svg root.
        nodeContainer.root().set(SvgSvgElement())

        if (this::_nativeLayer.isInitialized) {
            _nativeLayer.detach()
        }
    }

    private fun needRedraw() {
        if (!disposed) {
            skiaLayer.needRedraw()
        }
    }

    companion object {
        private fun render(elements: List<Element>, canvas: Canvas) {
            elements.forEach { element ->
                render(element, canvas)
            }
        }

        private fun render(element: Element, canvas: Canvas) {
            canvas.save()
            canvas.concat(element.transform)

            element.clipPath?.let(canvas::clipPath)
            val globalAlphaSet = element.opacity?.let {
                val paint = Paint().apply {
                    setAlphaf(it)
                }
                canvas.saveLayer(null, paint)
            }

            element.render(canvas)
            if (element is Container) {
                render(element.children, canvas)
            }

            globalAlphaSet?.let { canvas.restore() }

            canvas.restore()
        }

        private fun highlightElement(element: Element?, canvas: Canvas) {
            element?.let {
                val paint = Paint().apply {
                    setStroke(true)
                    strokeWidth = 3f
                    pathEffect = makeDash(floatArrayOf(5f, 5f), 0.0f)
                    color = Color.RED
                }
                canvas.drawRect(it.screenBounds, paint)
                paint.close()
            }
        }
    }
}
