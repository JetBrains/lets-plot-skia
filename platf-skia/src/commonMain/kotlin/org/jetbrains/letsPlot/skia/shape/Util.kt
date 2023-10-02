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

internal const val scaleX = 0
internal const val skewX = 1
internal const val translateX = 2
internal const val skewY = 3
internal const val scaleY = 4
internal const val translateY = 5
internal const val persp0 = 6
internal const val persp1 = 7
internal const val persp2 = 8


internal fun Matrix33.apply(sx: Float, sy: Float): Point {
    val x = sdot(sx, mat[scaleX], sy, mat[skewX]) + mat[org.jetbrains.letsPlot.skia.shape.translateX]
    val y = sdot(sx, mat[skewY], sy, mat[scaleY]) + mat[org.jetbrains.letsPlot.skia.shape.translateY]
    val z = (sdot(sx, mat[persp0], sy, mat[persp1]) + mat[persp2]).let { if (it != 0f) 1 / it else it }
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

internal val Matrix33.translateX get() = mat[org.jetbrains.letsPlot.skia.shape.translateX]
internal val Matrix33.translateY get() = mat[org.jetbrains.letsPlot.skia.shape.translateY]

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