/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.letsPlot.skia.svg.mapper.DebugOptions.USE_EXPLICIT_CTM_INSTEAD_OF_CANVAS_CONCAT
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
            if (USE_EXPLICIT_CTM_INSTEAD_OF_CANVAS_CONCAT) {
                canvas.setMatrix(ctm)
            } else {
                transform.let(canvas::concat)
            }

            clipPath?.let(canvas::clipPath)
            doDraw(canvas)
            canvas.restore()
        }

        override fun onGetBounds(): Rect = screenBounds
    }

    var transform: Matrix33 by visualProp(Matrix33.IDENTITY)
    var styleClass: List<String>? by visualProp(null)
    var clipPath: SkPath? by visualProp(null, managed = true)
    val drawable: Drawable by visualProp(_drawable, managed = true)

    var parent: Parent? by visualProp(null)

    val parents: List<Parent> by dependencyProp(Element::parent) {
        val parents = parent?.parents ?: emptyList()
        parents + listOfNotNull(parent)
    }

    val ctm: Matrix33 by dependencyProp(Element::parent, Element::transform) {
        val parentCtm = parent?.ctm ?: Matrix33.IDENTITY
        parentCtm.makeConcat(transform)
    }

    open val localBounds: Rect = Rect.Companion.makeWH(0f, 0f)
    open val screenBounds: Rect
        get() = ctm.apply(localBounds)

    override fun doNeedRedraw() {
        drawable.notifyDrawingChanged()
    }

    open fun doDraw(canvas: Canvas) {}

    override fun repr(): String? {
        return transform.mat.let {", transform: ${ it.joinToString(transform = Float::toString) }" }
    }
}
