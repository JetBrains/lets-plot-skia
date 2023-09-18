/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Rect

internal typealias SkPath = org.jetbrains.skia.Path

internal abstract class Element() : Node() {
    var transform: Matrix33 by visualProp(Matrix33.IDENTITY)
    var styleClass: List<String>? by visualProp(null)
    var clipPath: SkPath? by visualProp(null, managed = true)
    var parent: Container? by visualProp(null)

    val parents: List<Container> by computedProp(Element::parent) {
        val parents = parent?.parents ?: emptyList()
        parents + listOfNotNull(parent)
    }

    // Not affected by org.jetbrains.skiko.SkiaLayer.getContentScale
    // (see org.jetbrains.letsPlot.skia.svg.view.SvgSkikoView.onRender)
    val ctm: Matrix33 by computedProp(Element::parent, Element::transform) {
        val parentCtm = parent?.ctm ?: Matrix33.IDENTITY
        parentCtm.makeConcat(transform)
    }

    open val localBounds: Rect = Rect.Companion.makeWH(0f, 0f)

    // Not affected by org.jetbrains.skiko.SkiaLayer.getContentScale
    // (see org.jetbrains.letsPlot.skia.svg.view.SvgSkikoView.onRender)
    open val screenBounds: Rect
        get() = ctm.apply(localBounds)

    fun render(canvas: Canvas) {
        if (!isVisible) return

        canvas.save()
        canvas.concat(ctm) // Apply global transform (like scaling if run on HiDPI monitors)
        clipPath?.let(canvas::clipPath)
        onRender(canvas)
        canvas.restore()
    }

    protected open fun onRender(canvas: Canvas) {}

    override fun repr(): String? {
        return ctm.mat.let {", ctm: ${ it.joinToString(transform = Float::toString) }" } + ", $screenBounds"
    }
}
