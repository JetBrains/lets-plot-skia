/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.skia.*
import org.jetbrains.skia.PathEffect.Companion.makeDash
import kotlin.math.max
import kotlin.math.min

internal fun sdot(a: Float, b: Float, c: Float, d: Float): Float {
    return a * b + c * d
}

internal const val SCALE_X = 0
internal const val SKEW_X = 1
internal const val TRANSLATE_X = 2
internal const val SKEW_Y = 3
internal const val SCALE_Y = 4
internal const val TRANSLATE_Y = 5
internal const val PERSP0 = 6
internal const val PERSP1 = 7
internal const val PERSP2 = 8


internal fun Matrix33.apply(sx: Float, sy: Float): Point {
    val x = sdot(sx, scaleX, sy, skewX) + translateX
    val y = sdot(sx, skewY, sy, scaleY) + translateY
    val z = (sdot(sx, persp0, sy, persp1) + persp2).let { if (it != 0f) 1 / it else it }
    return Point(x * z, y * z)
}

internal fun Matrix33.with(idx: Int, v: Float): Matrix33 {
    return Matrix33(*mat).also { it.mat[idx] = v }
}

internal fun Matrix33.apply(r: Rect): Rect {
    val lt = apply(r.left, r.top)
    val rt = apply(r.right, r.top)
    val rb = apply(r.right, r.bottom)
    val lb = apply(r.left, r.bottom)

    val xs = listOf(lt.x, rt.x, rb.x, lb.x)
    val ys = listOf(lt.y, rt.y, rb.y, lb.y)

    return Rect.makeLTRB(xs.min(), ys.min(), xs.max(), ys.max())
}

internal val Matrix33.translateX get() = mat[TRANSLATE_X]
internal val Matrix33.translateY get() = mat[TRANSLATE_Y]

internal val Matrix33.scaleX get() = mat[SCALE_X]
internal val Matrix33.scaleY get() = mat[SCALE_Y]

internal val Matrix33.skewX get() = mat[SKEW_X]
internal val Matrix33.skewY get() = mat[SKEW_Y]

internal val Matrix33.persp0 get() = mat[PERSP0]
internal val Matrix33.persp1 get() = mat[PERSP1]
internal val Matrix33.persp2 get() = mat[PERSP2]

internal fun union(rects: List<Rect>): Rect? =
    rects.fold<Rect, Rect?>(null) { acc, rect ->
        if (acc != null) {
            Rect.makeLTRB(
                min(rect.left, acc.left),
                min(rect.top, acc.top),
                max(rect.right, acc.right),
                max(rect.bottom, acc.bottom)
            )
        } else {
            rect
        }
    }

internal fun breadthFirstTraversal(element: Element): Sequence<Element> {
    fun enumerate(element: Element): Sequence<Element> {
        return when (element) {
            is Container -> element.children.asSequence() + element.children.asSequence().flatMap(::enumerate)
            else -> emptySequence()
        }
    }

    return sequenceOf(element) + enumerate(element)
}

internal fun reversedBreadthFirstTraversal(element: Element): Sequence<Element> {
    fun enumerate(element: Element): Sequence<Element> {
        return when (element) {
            is Container -> {
                val reversed = element.children.asReversed().asSequence()
                reversed.flatMap(::enumerate) + reversed
            }
            else -> emptySequence()
        }
    }

    return enumerate(element) + sequenceOf(element)
}

internal fun depthFirstTraversal(element: Element): Sequence<Element> {
    fun enumerate(el: Element): Sequence<Element> {
        return when (el) {
            is Container -> sequenceOf(el) + el.children.asSequence().flatMap(::enumerate)
            else -> sequenceOf(el)
        }
    }

    return enumerate(element)
}

internal fun reversedDepthFirstTraversal(element: Element): Sequence<Element> {
    fun enumerate(el: Element): Sequence<Element> {
        return when (el) {
            is Container -> el.children.asReversed().asSequence().flatMap(::enumerate) + sequenceOf(el)
            else -> sequenceOf(el)
        }
    }

    return enumerate(element)
}

fun Matrix33.repr(): String {
    val elements = mutableListOf<String>()
    if (translateX != 0f || translateY != 0f) {
        elements += "translate($translateX, $translateY)"
    }

    if (scaleX != 1f || scaleY != 1f) {
        elements += "scale($scaleX, $scaleY)"
    }

    if (skewX != 0f || skewY != 0f) {
        elements += "skew($skewX, $skewY)"
    }

    if (elements.isEmpty()) {
        return "identity"
    }

    return elements.joinToString(separator = " ")
}

fun Rect.contains(x: Float, y: Float): Boolean {
    return x in left..right && y in top..bottom
}

fun Rect.contains(x: Int, y: Int): Boolean {
    return x.toFloat() in left..right && y.toFloat() in top..bottom
}

fun strokePaint(
    stroke: Color4f? = null,
    strokeWidth: Float = 1f,
    strokeOpacity: Float = 1f,
    strokeDashArray: List<Float>? = null,
    strokeDashOffset: Float = 0f, // not mandatory, default works fine
    strokeMiter: Float? = null
) : Paint? {
    if (stroke == null) return null
    if (strokeOpacity == 0f) return null

    if (strokeWidth == 0f) {
        // Handle zero width manually, because Skia threatens 0 as "hairline" width, i.e. 1 pixel.
        // Source: https://api.skia.org/classSkPaint.html#af08c5bc138e981a4e39ad1f9b165c32c
        return null
    }

    val paint = Paint()
    paint.setStroke(true)
    paint.color4f = stroke.withA(strokeOpacity)
    paint.strokeWidth = strokeWidth
    strokeMiter?.let { paint.strokeMiter = it }
    strokeDashArray?.let { paint.pathEffect = makeDash(it.toFloatArray(), strokeDashOffset) }
    return paint
}

fun fillPaint(fill: Color4f? = null, fillOpacity: Float = 1f): Paint? {
    if (fill == null) return null

    return Paint().also { paint ->
        paint.color4f = fill.withA(fillOpacity)
    }
}

fun toFontStyle(face: FontFace): FontStyle = when {
    face.bold && !face.italic -> FontStyle.BOLD
    face.bold && face.italic -> FontStyle.BOLD_ITALIC
    !face.bold && face.italic -> FontStyle.ITALIC
    !face.bold && !face.italic -> FontStyle.NORMAL
    else -> error("Unknown fontStyle: `$face`")
}
