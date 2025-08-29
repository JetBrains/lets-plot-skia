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
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PathEffect.Companion.makeDash
import kotlin.math.ceil

abstract class SkiaSvgView() : Disposable {
    private var disposed = false
    private val nodeContainer = SvgNodeContainer(SvgSvgElement())  // attach root
    private var rootElement: Pane = Pane()
    private val fontManager = FontManager()
    private var eventReg: Registration = Registration.EMPTY
    private var clickedElement: Element? = null

    var width: Int = 0
        private set
    var height: Int = 0
        private set

    var eventDispatcher: SvgViewEventDispatcher? = null
        set(value) {
            eventReg.remove()
            if (value != null) {
                eventReg = value.addEventHandler(MouseEventSpec.MOUSE_CLICKED, object : EventHandler<MouseEvent> {
                    override fun onEvent(event: MouseEvent) {
                        reversedDepthFirstTraversal(rootElement)
                            .filterNot { it.isMouseTransparent }
                            .firstOrNull() { it.screenBounds.contains(event.x, event.y) }
                            ?.let {
                                clickedElement = if (clickedElement == it) null else it
                                needRedraw()
                            }
                    }
                })
            }
            field = value
        }

    var svg: SvgSvgElement
        get() = nodeContainer.root().get()
        set(value) {
            nodeContainer.root().set(value)
            val rootMapper = SvgSvgElementMapper(value, SvgSkiaPeer(fontManager))
            rootMapper.attachRoot(MappingContext())
            rootElement = rootMapper.target

            width = value.width().get()?.let { ceil(it).toInt() } ?: 0
            height = value.height().get()?.let { ceil(it).toInt() } ?: 0

            needRedraw()
        }

    init {
        nodeContainer.addListener(object : SvgNodeContainerListener {
            override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) = needRedraw()
            override fun onNodeAttached(node: SvgNode) = needRedraw()
            override fun onNodeDetached(node: SvgNode) = needRedraw()
        })
    }

    protected abstract fun needRedraw()

    protected fun renderIntern(canvas: Canvas) {
        if (disposed) {
            return
        }

        render(rootElement, canvas)

        if (DebugOptions.DEBUG_DRAWING_ENABLED) {
            highlightElement(clickedElement, canvas)
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

    }

    companion object {
        private fun render(elements: List<Element>, canvas: Canvas) {
            elements.forEach { element ->
                render(element, canvas)
            }
        }

        private fun render(element: Element, canvas: Canvas) {
            if (!element.isVisible) {
                return
            }

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

    protected fun onMouseEvent(spec: MouseEventSpec, event: MouseEvent) {
        if (spec == MouseEventSpec.MOUSE_CLICKED) {
            reversedDepthFirstTraversal(rootElement)
                .filterNot { it.isMouseTransparent }
                .firstOrNull() { it.screenBounds.contains(event.x, event.y) }
                ?.let { it.href?.let(::onHrefClick) }
        }
        eventDispatcher?.dispatchMouseEvent(spec, event)
    }

    abstract fun onHrefClick(href: String)
}
