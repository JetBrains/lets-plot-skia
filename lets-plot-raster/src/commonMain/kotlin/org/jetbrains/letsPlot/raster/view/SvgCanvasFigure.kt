/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.view

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.AnimationProvider
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNodeContainer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.raster.mapping.svg.SvgCanvasPeer
import org.jetbrains.letsPlot.raster.mapping.svg.SvgSvgElementMapper
import org.jetbrains.letsPlot.raster.mapping.svg.TextMeasurer
import org.jetbrains.letsPlot.raster.shape.Container
import org.jetbrains.letsPlot.raster.shape.Element
import org.jetbrains.letsPlot.raster.shape.Pane
import org.jetbrains.letsPlot.raster.shape.transform
import kotlin.math.ceil

class SvgCanvasFigure(
    val svgSvgElement: SvgSvgElement
) : CanvasFigure {
    internal lateinit var rootMapper: SvgSvgElementMapper // = SvgSvgElementMapper(svgSvgElement, canvasPeer)
    private val rootElement: Pane get() = rootMapper.target

    val width = svgSvgElement.width().get()?.let { ceil(it).toInt() } ?: 0
    val height = svgSvgElement.height().get()?.let { ceil(it).toInt() } ?: 0

    override fun bounds(): ReadableProperty<Rectangle> {
        TODO("Not yet implemented")
    }

    override fun mapToCanvas(canvasControl: CanvasControl): Registration {
        val canvasPeer = SvgCanvasPeer(
            textMeasurer = TextMeasurer.create(canvasControl)
        )

        rootMapper = SvgSvgElementMapper(svgSvgElement, canvasPeer)
        val nodeContainer = SvgNodeContainer(svgSvgElement)  // attach root
        rootMapper.attachRoot(MappingContext())

        val canvas = canvasControl.createCanvas(Vector(width, height))
        val anim = canvasControl.createAnimationTimer(object : AnimationProvider.AnimationEventHandler {
            override fun onEvent(millisTime: Long): Boolean {
                canvas.context2d.clearRect(DoubleRectangle(0.0, 0.0, width.toDouble(), height.toDouble()))
                render(rootElement, canvas)
                return true
            }
        })

        canvasControl.addChild(canvas)
        render(rootElement, canvas)

        anim.start()

        return Registration.Companion.EMPTY
    }

    private fun render(elements: List<Element>, canvas: Canvas) {
        elements.forEach { element ->
            render(element, canvas)
        }
    }

    private fun render(element: Element, canvas: Canvas) {
        if (!element.isVisible) {
            return
        }

        canvas.context2d.save()
        canvas.context2d.transform(element.transform)

        //element.clipPath?.let(canvas::clipPath)
        //val globalAlphaSet = element.opacity?.let {
        //    val paint = Paint().apply {
        //        setAlphaf(it)
        //    }
        //    ctx.saveLayer(null, paint)
        //}

        element.render(canvas)
        if (element is Container) {
            render(element.children, canvas)
        }

        //globalAlphaSet?.let { canvas.restore() }

        canvas.context2d.restore()
    }

//private fun highlightElement(element: Element?, canvas: Canvas) {
//    element?.let {
//        val paint = Paint().apply {
//            setStroke(true)
//            strokeWidth = 3f
//            pathEffect = makeDash(floatArrayOf(5f, 5f), 0.0f)
//            color = Color.RED
//        }
//        canvas.drawRect(it.screenBounds, paint)
//        paint.close()
//    }
//}

}