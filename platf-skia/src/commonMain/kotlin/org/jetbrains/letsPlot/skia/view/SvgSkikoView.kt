/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.view

import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.skia.mapping.svg.DebugOptions
import org.jetbrains.letsPlot.skia.mapping.svg.DebugOptions.drawBoundingBoxes
import org.jetbrains.letsPlot.skia.mapping.svg.SvgSkiaPeer
import org.jetbrains.letsPlot.skia.mapping.svg.SvgSvgElementMapper
import org.jetbrains.letsPlot.skia.shape.Container
import org.jetbrains.letsPlot.skia.shape.Element
import org.jetbrains.letsPlot.skia.shape.Pane
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Matrix33.Companion.IDENTITY
import org.jetbrains.skia.Matrix33.Companion.makeScale
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoGestureEvent
import org.jetbrains.skiko.SkikoPointerEvent
import org.jetbrains.skiko.SkikoView
import kotlin.math.ceil

abstract class SvgSkikoView(
    svg: SvgSvgElement,
    val eventDispatcher: SkikoViewEventDispatcher?
) : SkikoView, Disposable {

    private val nodeContainer = SvgNodeContainer(svg)  // attach root
    private val rootElement: Pane
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

    init {
        val rootMapper = SvgSvgElementMapper(svg, SvgSkiaPeer())
        rootMapper.attachRoot(MappingContext())
        rootElement = rootMapper.target

        nodeContainer.addListener(object : SvgNodeContainerListener {
            override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) { needRedraw()}
            override fun onNodeAttached(node: SvgNode) { needRedraw() }
            override fun onNodeDetached(node: SvgNode) { needRedraw() }
        })
    }

    protected abstract fun createSkiaLayer(view: SvgSkikoView): SkiaLayer

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        if (width == 0 && height == 0) {
            // Skiko may call onRender before OpenGL context is initialized.
            // In this case we request another render call as soon as context is initialized.
            // Skiko itself won't call onRender again when context is initialized.
            needRedraw()
            return
        }

        val scaleMatrix = IDENTITY.takeIf { skiaLayer.contentScale == 1f } ?: makeScale(skiaLayer.contentScale)

        render(rootElement, canvas, scaleMatrix)

        if (DebugOptions.DEBUG_DRAWING_ENABLED) {
            drawBoundingBoxes(rootElement, canvas, scaleMatrix)
        }
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

    private fun needRedraw() {
        if (!disposed) {
            skiaLayer.needRedraw()
        }
    }

    companion object {
        private fun render(elements: List<Element>, canvas: Canvas, scaleMatrix: Matrix33) {
            elements.forEach { element ->
                render(element, canvas, scaleMatrix)
            }
        }

        private fun render(element: Element, canvas: Canvas, scaleMatrix: Matrix33) {
            canvas.save()
            canvas.setMatrix(scaleMatrix.makeConcat(element.ctm))
            element.clipPath?.let(canvas::clipPath)

            if (element is Container) {
                render(element.children, canvas, scaleMatrix)
            }
            element.render(canvas)

            canvas.restore()
        }
    }
}