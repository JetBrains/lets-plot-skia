/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.PathFillMode
import org.jetbrains.skia.Rect

internal class Path : Figure() {
    var fillRule: PathFillMode? by visualProp(null)
    var skiaPath: SkPath? by visualProp(null, managed = true)

    private val path: SkPath? by computedProp(Path::fillRule, Path::skiaPath, managed = true) {
        val skiaPath = skiaPath ?: return@computedProp null

        SkPath().apply {
            fillRule?.let { fillMode = it }
            addPath(skiaPath)
        }
    }

    override fun onRender(canvas: Canvas) {
        val path = path ?: return

        fillPaint?.let { canvas.drawPath(path, it) }
        strokePaint?.let { canvas.drawPath(path, it) }
    }

    override val localBounds: Rect
        get() {
            // `paint.getFillPath()` is not available in skiko v. 0.7.63
//            return (strokePaint?.getFillPath(path) ?: path).bounds

            val path = path ?: return Rect.Companion.makeWH(0.0f, 0.0f)
            val strokeWidth = strokePaint?.strokeWidth ?: return path.bounds

            return path.bounds.inflate(strokeWidth / 2f)
        }
}