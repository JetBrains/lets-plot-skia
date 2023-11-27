/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Point
import org.jetbrains.skia.Rect
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

internal fun childrenDeepTraversal(element: Element): Sequence<Element> {
    return when (element) {
        is Container -> element.children.asSequence() + element.children.flatMap(::childrenDeepTraversal)
        else -> emptySequence()
    }
}

internal fun depthFirstTraversal(element: Element, visit: (Element) -> Unit) {
    visit(element)
    if (element is Container) {
        depthFirstTraversal(element.children, visit)
    }
}

internal fun depthFirstTraversal(elements: List<Element>, visit: (Element) -> Unit) {
    elements.forEach {
        when (it) {
            is Container -> {
                visit(it)
                depthFirstTraversal(it.children, visit)
            }

            else -> visit(it)
        }
    }
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