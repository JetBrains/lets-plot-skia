package jetbrains.datalore.vis.svgMapper.skia

import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import jetbrains.datalore.base.event.KeyModifiers.Companion.emptyModifiers
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.skia.mapper.SvgSkiaPeer
import jetbrains.datalore.vis.svgMapper.skia.mapper.SvgSvgElementMapper
import org.jetbrains.skia.Canvas
import org.jetbrains.skiko.*

internal class SkiaWidget(
    private val svg: SvgSvgElement,
    val nativeLayer: SkiaLayer,
    val x: Double = 0.0,
    val y: Double = 0.0,
    initialize: (SkiaLayer, SkikoView) -> Unit = { _, _ -> }// FIXME: https://github.com/JetBrains/skiko/issues/614
) {
    @Suppress("unused")
    private val nodeContainer = SvgNodeContainer(svg)  // attach root
    private val rootMapper = SvgSvgElementMapper(svg, SvgSkiaPeer())
    private var mouseEventHandler: (MouseEventSpec, MouseEvent) -> Unit = EMPTY_MOUSE_EVENT_HANDLER

    val skikoView = object : SkikoView {
        override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
            canvas.scale(nativeLayer.contentScale, nativeLayer.contentScale)
            canvas.drawDrawable(rootMapper.target)
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

    init {
        rootMapper.attachRoot(MappingContext())
        initialize(nativeLayer, skikoView)
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
