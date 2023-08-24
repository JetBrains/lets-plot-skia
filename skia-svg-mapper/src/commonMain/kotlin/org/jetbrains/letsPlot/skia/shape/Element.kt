/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.skia.svg.mapper.DebugOptions.USE_SCREEN_TRANSFORM
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Drawable
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Rect

internal typealias SkPath = org.jetbrains.skia.Path

internal abstract class Element : Node() {
    private val _drawable = object : Drawable() {
        override fun onDraw(canvas: Canvas?) {
            if (canvas == null) return
            if (!isVisible) return

            canvas.save()
            if (USE_SCREEN_TRANSFORM) {
                canvas.setMatrix(ctm)
            } else {
                localTransform.let(canvas::concat)
            }

            clipPath?.let(canvas::clipPath)
            doDraw(canvas)
            canvas.restore()
        }

        override fun onGetBounds(): Rect = screenBounds
    }

    var styleClass: List<String>? by visualProp(null)
    var transform: Matrix33? by visualProp(null)
    var clipPath: SkPath? by visualProp(null, managed = true)
    var isVisible: Boolean by visualProp(true)
    val drawable: Drawable by visualProp(_drawable, managed = true)

    var parent: Parent? by visualProp(null)

    // TODO: perf. Update only on a tree change
    private val parents: List<Parent>
        get() {
            var p = parent
            val parents = mutableListOf<Parent>()
            while (p != null) {
                parents.add(0, p)
                p = p.parent
            }
            return parents
        }

    // TODO: perf. Update only if changed
    open val localBounds: Rect = Rect.Companion.makeWH(0f, 0f)

    // TODO: perf.
    // Mostly just an SVG transform.
    // Single exception is the SvgSvgElement/Pane. It doesn't support transform, yet uses x, y for implicit translate
    open val localTransform: Matrix33
        get() = transform ?: Matrix33.IDENTITY

    // TODO: perf. Update only if changed
    open val screenBounds: Rect
        get() = ctm.apply(localBounds)

    // TODO: perf. Cache. Update only on a transform change in a tree.
    // current transformation matrix
    val ctm: Matrix33
        get() =
            parents
                .mapNotNull(Parent::localTransform)
                .fold(Matrix33.IDENTITY, Matrix33::makeConcat)
                .makeConcat(localTransform)

    override fun doNeedRedraw() {
        drawable.notifyDrawingChanged()
    }

    open fun doDraw(canvas: Canvas) {}

    override fun toString(): String {
        val repr = repr()?.let { ", $it" }
        return "class: ${this::class.simpleName}${repr ?: ""}${
            localTransform.mat.let {
                ", transform: ${
                    it.joinToString(
                        transform = Float::toString
                    )
                }"
            }
        }"
    }
}
