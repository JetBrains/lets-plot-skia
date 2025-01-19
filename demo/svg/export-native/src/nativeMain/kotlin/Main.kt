/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import okio.Path.Companion.toPath
import org.jetbrains.skia.*


fun main(args: Array<String>) {
    try {
        // Stage 1
        val bitmap = Bitmap().apply {
            setImageInfo(
                ImageInfo(
                    width = 400,
                    height = 400,
                    colorType = ColorType.RGBA_8888,
                    alphaType = ColorAlphaType.UNPREMUL,
                )
            )
            allocPixels()
        }

        Canvas(bitmap).apply {
            clear(0xFF00FFFF.toInt())
            drawCircle(200.0f, 200.0f, 100.0f, Paint())
        }

        val image = Image.makeFromBitmap(bitmap)

        okio.FileSystem.SYSTEM.write("output.png".toPath()) {
            write(image.encodeToData(EncodedImageFormat.PNG)!!.bytes)
        }

        // Stage 2
        /*
    val svgRoot = SvgSvgElement(
        width = 200.0,
        height = 400.0
    ).apply {
        children().addAll(listOf(
            SvgRectElement(
                x = 10.0,
                y = 100.0,
                width = 180.0,
                height = 180.0
            ).apply {
                fill().set(SvgColors.ORANGE)
            },
            SvgEllipseElement(
                cx = 100.0,
                cy = 190.0,
                rx = 50.0,
                ry = 50.0
            ).apply {
                fill().set(SvgColors.DARK_RED)
            },
            SvgTextElement(100.0, 195.0, "Hello, World").apply {
                textAnchor().set(SVG_TEXT_ANCHOR_MIDDLE)
                fillColor().set(Color.WHITE)
            },
        )
        )
    }
    */

        // Stage 3
        /*
    SvgSkikoView.render(svgRoot, canvas, Matrix33.IDENTITY)
    */

        println("Hello, world")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}