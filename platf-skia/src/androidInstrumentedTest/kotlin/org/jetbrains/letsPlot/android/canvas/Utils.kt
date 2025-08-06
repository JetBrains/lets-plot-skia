package org.jetbrains.letsPlot.android.canvas

import android.graphics.Bitmap
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class ImageResultTestWatcher(private val imageComparer: ImageComparer) : TestWatcher() {
    override fun finished(description: Description) {
        super.finished(description)

        val imagePaths = imageComparer.diffImagePaths
        if (imagePaths.isNotEmpty()) {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            val bundle = Bundle()
            bundle.putStringArray("DIFF_IMAGE_PATHS", imagePaths.toTypedArray())
            instrumentation.sendStatus(0, bundle) // Use sendStatus instead of finish
            imageComparer.diffImagePaths.clear()
        }
    }
}



val AndroidCanvas.img: Bitmap get() = this.platformBitmap

var Context2d.lineWidth: Double
    get() = error("lineWidth is write only")
    set(value) {
        setLineWidth(value)
    }

var Context2d.fillStyle: Any?
    get() = error("fillStyle is write only")
    set(value) {
        val color = when (value) {
            is Color -> value
            is String -> Colors.parseColor(value)
            null -> null
            else -> error("Unsupported fill style: $value")
        }

        setFillStyle(color)
    }

var Context2d.strokeStyle: Any?
    get() = error("strokeStyle is write only")
    set(value) {
        val color = when (value) {
            is Color -> value
            is String -> Colors.parseColor(value)
            null -> null
            else -> error("Unsupported fill style: $value")
        }

        setStrokeStyle(color)
    }

fun Context2d.moveTo(x: Number, y: Number) {
    moveTo(x.toDouble(), y.toDouble())
}

fun Context2d.lineTo(x: Number, y: Number) {
    lineTo(x.toDouble(), y.toDouble())
}

fun Context2d.bezierCurveTo(
    cp1x: Number,
    cp1y: Number,
    cp2x: Number,
    cp2y: Number,
    x: Number,
    y: Number
) {
    bezierCurveTo(
        cp1x.toDouble(),
        cp1y.toDouble(),
        cp2x.toDouble(),
        cp2y.toDouble(),
        x.toDouble(),
        y.toDouble()
    )
}

fun Context2d.ellipse(
    x: Number,
    y: Number,
    radiusX: Number,
    radiusY: Number,
    rotation: Number,
    startAngle: Number,
    endAngle: Number,
    anticlockwise: Boolean = false
) {
    ellipse(
        x.toDouble(),
        y.toDouble(),
        radiusX.toDouble(),
        radiusY.toDouble(),
        rotation.toDouble(),
        startAngle.toDouble(),
        endAngle.toDouble(),
        anticlockwise
    )
}

fun Context2d.translate(x: Number, y: Number) {
    translate(x.toDouble(), y.toDouble())
}

fun Context2d.arc(
    x: Number,
    y: Number,
    radius: Number,
    startAngle: Number,
    endAngle: Number,
    anticlockwise: Boolean = false
) {
    arc(
        x.toDouble(),
        y.toDouble(),
        radius.toDouble(),
        startAngle.toDouble(),
        endAngle.toDouble(),
        anticlockwise
    )
}

fun Context2d.transform(
    sx: Number,
    ry: Number,
    rx: Number,
    sy: Number,
    tx: Number,
    ty: Number
) {
    transform(
        sx.toDouble(),
        ry.toDouble(),
        rx.toDouble(),
        sy.toDouble(),
        tx.toDouble(),
        ty.toDouble()
    )
}
