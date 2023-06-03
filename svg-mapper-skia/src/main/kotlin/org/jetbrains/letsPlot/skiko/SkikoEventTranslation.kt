package org.jetbrains.letsPlot.skiko

import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import org.jetbrains.skiko.*

internal fun SkikoGestureEvent.translate(): Pair<MouseEventSpec, MouseEvent>? {
    return when (this.kind) {
        SkikoGestureEventKind.LONGPRESS -> MouseEventSpec.MOUSE_LEFT
        SkikoGestureEventKind.PAN -> MouseEventSpec.MOUSE_MOVED
        SkikoGestureEventKind.TAP -> MouseEventSpec.MOUSE_MOVED
        else -> null
    }?.let { mouseEventSpec ->
        mouseEventSpec to this.toMouseEvent()
    }
}

private fun SkikoGestureEvent.toMouseEvent(): MouseEvent {
    return MouseEvent(
        x = x.toInt(),
        y = y.toInt(),
        button = Button.NONE,
        modifiers = KeyModifiers.emptyModifiers()
    )
}

internal fun SkikoPointerEvent.translate(): Pair<MouseEventSpec, MouseEvent>? {
    return when (this.kind) {
        SkikoPointerEventKind.UP -> MouseEventSpec.MOUSE_RELEASED
        SkikoPointerEventKind.DOWN -> MouseEventSpec.MOUSE_PRESSED
        SkikoPointerEventKind.MOVE -> MouseEventSpec.MOUSE_MOVED
        SkikoPointerEventKind.DRAG -> MouseEventSpec.MOUSE_DRAGGED
        SkikoPointerEventKind.ENTER -> MouseEventSpec.MOUSE_ENTERED
        SkikoPointerEventKind.EXIT -> MouseEventSpec.MOUSE_LEFT
        else -> null
    }?.let { mouseEventSpec ->
        mouseEventSpec to this.toMouseEvent()
    }
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
