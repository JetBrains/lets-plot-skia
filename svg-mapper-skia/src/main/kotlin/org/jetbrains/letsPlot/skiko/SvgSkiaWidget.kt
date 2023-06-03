package org.jetbrains.letsPlot.skiko

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.SvgSvgElement
import org.jetbrains.letsPlot.skia.mapper.DebugOptions
import org.jetbrains.letsPlot.skia.mapper.SvgSkiaPeer
import org.jetbrains.letsPlot.skia.mapper.SvgSvgElementMapper
import org.jetbrains.letsPlot.skia.pane.*
import org.jetbrains.skia.Canvas
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoGestureEvent
import org.jetbrains.skiko.SkikoPointerEvent
import org.jetbrains.skiko.SkikoView
import kotlin.random.Random

class SvgSkiaWidget constructor(
    private val svg: SvgSvgElement,
    val nativeLayer: SkiaLayer,
    initialize: (SkiaLayer, SkikoView) -> Unit = { _, _ -> }, // FIXME: https://github.com/JetBrains/skiko/issues/614
) {
    @Suppress("unused")
    private val nodeContainer = SvgNodeContainer(svg)  // attach root
    private val rootMapper = SvgSvgElementMapper(svg, SvgSkiaPeer())
    private var mouseEventHandler: (MouseEventSpec, MouseEvent) -> Unit = EMPTY_MOUSE_EVENT_HANDLER

    private val skikoView = object : SkikoView {
        override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
            canvas.scale(nativeLayer.contentScale, nativeLayer.contentScale)
            canvas.drawDrawable(rootMapper.target.drawable)
            //traverse(rootMapper.target).filter { it is Figure }.forEach { canvas.drawDrawable(it.drawable, it.screenTransform) }
        }

        override fun onGestureEvent(event: SkikoGestureEvent) {
//            when (event.kind) {
//                SkikoGestureEventKind.LONGPRESS -> MouseEventSpec.MOUSE_LEFT
//                SkikoGestureEventKind.PAN -> MouseEventSpec.MOUSE_MOVED
//                SkikoGestureEventKind.TAP -> MouseEventSpec.MOUSE_MOVED
//                else -> null
//            }?.let {
//                mouseEventHandler(it, event.toMouseEvent())
//                nativeLayer.needRedraw()
//            }
            event.translate()?.let {
                mouseEventHandler(it.first, it.second)
                nativeLayer.needRedraw()
            }
        }

        override fun onPointerEvent(event: SkikoPointerEvent) {
//            when (event.kind) {
//                SkikoPointerEventKind.UP -> MouseEventSpec.MOUSE_RELEASED
//                SkikoPointerEventKind.DOWN -> MouseEventSpec.MOUSE_PRESSED
//                SkikoPointerEventKind.MOVE -> MouseEventSpec.MOUSE_MOVED
//                SkikoPointerEventKind.DRAG -> MouseEventSpec.MOUSE_DRAGGED
//                SkikoPointerEventKind.ENTER -> MouseEventSpec.MOUSE_ENTERED
//                SkikoPointerEventKind.EXIT -> MouseEventSpec.MOUSE_LEFT
//                else -> null
//            }?.let {
//                mouseEventHandler(it, event.toMouseEvent())
//                nativeLayer.needRedraw()
//            }
            event.translate()?.let {
                mouseEventHandler(it.first, it.second)
                nativeLayer.needRedraw()
            }
        }
    }

    private fun traverse(element: Element): Sequence<Element> {
        return when (element) {
            is Parent -> element.children.asSequence() + element.children.asSequence().flatMap(::traverse)
            else -> sequenceOf(element)
        }
    }

    init {
        rootMapper.attachRoot(MappingContext())
        initialize(nativeLayer, skikoView)

        if (DebugOptions.DRAW_BBOX) {
            val bboxes = traverse(rootMapper.target)
                .filterNot { it is Group && it.transform == null } // fills whole area and distracts other bboxes
                .filterNot { it is Rectangle && it.x == 0f && it.y == 0f } // fills whole area and distracts other bboxes
                .map {
                    val bounds = it.screenBounds
                    SvgRectElement().apply {
                        x().set(bounds.left.toDouble())
                        y().set(bounds.top.toDouble())
                        width().set(bounds.width.toDouble())
                        height().set(bounds.height.toDouble())

                        val color = when (it) {
                            is Group -> Color.BLACK
                            is Text -> Color.GREEN
                            is Rectangle -> Color.BLUE
                            is Circle -> Color.ORANGE
                            is Line -> Color.RED
                            else -> Color.MAGENTA
                        }.let { color -> Colors.darker(color, Random.nextDouble(from = 0.7, until = 1.0))!! }

                        strokeColor().set(color)
                        fillColor().set(color.changeAlpha(20))

                    }
                }.toList()

            svg.children().addAll(bboxes)
        }
    }

    fun setMouseEventListener(handler: (MouseEventSpec, MouseEvent) -> Unit) {
        mouseEventHandler = handler
    }

    fun width() = (svg.width().get() ?: 0.0)
    fun height() = (svg.height().get() ?: 0.0)


}

internal val EMPTY_MOUSE_EVENT_HANDLER: (MouseEventSpec, MouseEvent) -> Unit = { _, _ -> }
