package jetbrains.datalore.vis.svgMapper.skia

import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import jetbrains.datalore.base.event.KeyModifiers.Companion.emptyModifiers
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.skia.mapper.SvgSkiaPeer
import jetbrains.datalore.vis.svgMapper.skia.mapper.SvgSvgElementMapper
import jetbrains.datalore.vis.svgMapper.skia.mapper.drawing.*
import org.jetbrains.skia.Canvas
import org.jetbrains.skiko.*
import kotlin.random.Random

class SkiaWidget(
    private val svg: SvgSvgElement,
    val nativeLayer: SkiaLayer,
    val x: Double = 0.0,
    val y: Double = 0.0,
    initialize: (SkiaLayer, SkikoView) -> Unit = { _, _ -> }, // FIXME: https://github.com/JetBrains/skiko/issues/614
) {
    @Suppress("unused")
    private val nodeContainer = SvgNodeContainer(svg)  // attach root
    private val rootMapper = SvgSvgElementMapper(svg, SvgSkiaPeer())
    private var mouseEventHandler: (MouseEventSpec, MouseEvent) -> Unit = EMPTY_MOUSE_EVENT_HANDLER

    val skikoView = object : SkikoView {
        override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
            canvas.scale(nativeLayer.contentScale, nativeLayer.contentScale)
            canvas.drawDrawable(rootMapper.target.drawable)
        }

        override fun onGestureEvent(event: SkikoGestureEvent) {
            when (event.kind) {
                SkikoGestureEventKind.LONGPRESS -> MouseEventSpec.MOUSE_LEFT
                SkikoGestureEventKind.PAN -> MouseEventSpec.MOUSE_MOVED
                SkikoGestureEventKind.TAP -> MouseEventSpec.MOUSE_MOVED
                else -> null
            }?.let {
                mouseEventHandler(it, event.toMouseEvent())
                nativeLayer.needRedraw()
            }
        }

        override fun onPointerEvent(event: SkikoPointerEvent) {
            when (event.kind) {
                SkikoPointerEventKind.UP -> MouseEventSpec.MOUSE_RELEASED
                SkikoPointerEventKind.DOWN -> MouseEventSpec.MOUSE_PRESSED
                SkikoPointerEventKind.MOVE -> MouseEventSpec.MOUSE_MOVED
                SkikoPointerEventKind.DRAG -> MouseEventSpec.MOUSE_DRAGGED
                SkikoPointerEventKind.ENTER -> MouseEventSpec.MOUSE_ENTERED
                SkikoPointerEventKind.EXIT -> MouseEventSpec.MOUSE_LEFT
                else -> null
            }?.let {
                mouseEventHandler(it, event.toMouseEvent())
                nativeLayer.needRedraw()
            }
        }
    }

    private fun traverse(element: Element): List<Element> {
        return when (element) {
            is Parent -> element.children + element.children.flatMap(::traverse)
            else -> listOf(element)
        }
    }

    init {
        rootMapper.attachRoot(MappingContext())
        initialize(nativeLayer, skikoView)

        if (DebugOptions.DRAW_BBOX) {
            traverse(rootMapper.target)
                .filterNot { it is Group && it.transform == null }
                .filterNot { it is Rectangle && it.x == 0f && it.y == 0f }
                .forEach {
                    val bounds = it.screenBounds
                    svg.children().add(
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
                    )
                }
        }
    }

    fun setMouseEventListener(handler: (MouseEventSpec, MouseEvent) -> Unit) {
        mouseEventHandler = handler
    }

    fun width() = (svg.width().get() ?: 0.0)
    fun height() = (svg.height().get() ?: 0.0)
}

val EMPTY_MOUSE_EVENT_HANDLER: (MouseEventSpec, MouseEvent) -> Unit = { _, _ -> }

private fun SkikoGestureEvent.toMouseEvent(): MouseEvent {
    return MouseEvent(
        x = x.toInt(),
        y = y.toInt(),
        button = Button.NONE,
        modifiers = emptyModifiers()
    )
}

private fun SkikoPointerEvent.toMouseEvent(): MouseEvent {
    return MouseEvent(
        x = x.toInt(),
        y = y.toInt(),
        button = when (button) {
            SkikoMouseButtons.LEFT -> Button.LEFT
            SkikoMouseButtons.MIDDLE -> Button.MIDDLE
            SkikoMouseButtons.RIGHT -> Button.RIGHT
            SkikoMouseButtons.NONE -> Button.NONE
            else -> Button.NONE.also { println("Unsupported button: $button") }
        },
        modifiers = KeyModifiers(
            isCtrl = modifiers.has(SkikoInputModifiers.CONTROL),
            isAlt = modifiers.has(SkikoInputModifiers.ALT),
            isShift = modifiers.has(SkikoInputModifiers.SHIFT),
            isMeta = modifiers.has(SkikoInputModifiers.META)
        )
    )
}
