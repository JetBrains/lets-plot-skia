package org.jetbrains.letsPlot.android.canvas

import android.graphics.*
import android.os.Environment
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class ImageComparer(
    val diffImagePaths: MutableList<String> = mutableListOf()
) {
    fun assertImageEquals(expectedFileName: String, actualBitmap: Bitmap) {
        val testName = expectedFileName.removeSuffix(".bmp")
        val expectedPath = javaClass.classLoader?.getResourceAsStream(expectedFileName)
        val actualFilePath = "${testName}.bmp"

        val expectedBitmap = BitmapFactory.decodeStream(expectedPath)
            ?: error("Failed to read expected image: $expectedPath")

        try {
            val expected = exportPixels(expectedBitmap)
            val actual = exportPixels(actualBitmap)

            if (!comparePixelArrays(expected, actual, tolerance = 0)) {
                val diffFilePath = saveBitmap(createVisualDiff(expectedBitmap, actualBitmap), "${testName}_diff.bmp")
                diffImagePaths += diffFilePath // log diff image path to retrieve later from the device

                saveBitmap(actualBitmap, actualFilePath)

                error(
                    """Image mismatch.
                    |    Diff: $diffFilePath
                    |    Actual: $actualFilePath
                    |    Expected: $expectedPath""".trimMargin()
                )
            } else {
                println("Image comparison passed: $expectedPath")
            }
        } finally {
            expectedBitmap.recycle()
        }
    }

    private fun saveBitmap(bitmap: Bitmap, filename: String): String {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: error("Failed to get external files directory")

        val file = File(dir, filename)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file.absolutePath
    }


    private fun exportPixels(bitmap: Bitmap): IntArray {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return pixels
    }

    private fun pixelsEqual(p1: Int, p2: Int, tolerance: Int): Boolean {
        val r1 = Color.red(p1)
        val g1 = Color.green(p1)
        val b1 = Color.blue(p1)
        val a1 = Color.alpha(p1)

        val r2 = Color.red(p2)
        val g2 = Color.green(p2)
        val b2 = Color.blue(p2)
        val a2 = Color.alpha(p2)

        return abs(r1 - r2) <= tolerance &&
                abs(g1 - g2) <= tolerance &&
                abs(b1 - b2) <= tolerance &&
                abs(a1 - a2) <= tolerance
    }

    private fun comparePixelArrays(expected: IntArray, actual: IntArray, tolerance: Int = 0): Boolean {
        if (expected.size != actual.size) return false
        return expected.indices.all { pixelsEqual(expected[it], actual[it], tolerance) }
    }

    private fun createVisualDiff(expected: Bitmap, actual: Bitmap): Bitmap {
        val width = expected.width
        val height = expected.height
        val separatorWidth = 10

        val totalTopWidth = width * 2 + separatorWidth
        val totalHeight = height * 2 + separatorWidth

        val result = Bitmap.createBitmap(totalTopWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        // Fill white background
        canvas.drawColor(Color.WHITE)

        // Draw images side by side
        canvas.drawBitmap(expected, 0f, 0f, null)
        canvas.drawBitmap(actual, (width + separatorWidth).toFloat(), 0f, null)

        // Create vertical separator
        val separatorPaint = Paint().apply {
            color = Color.GRAY
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        // Draw diagonal lines in separator
        for (i in 0 until height step 4) {
            canvas.drawLine(
                width.toFloat(),
                i.toFloat(),
                (width + separatorWidth).toFloat(),
                (i + 4).toFloat(),
                separatorPaint
            )
        }

        // Create diff image
        val diff = createDiffImage(expected, actual)

        // Draw diff below with padding
        val diffX = (totalTopWidth - diff.width) / 2f
        canvas.drawBitmap(diff, diffX, (height + separatorWidth).toFloat(), null)

        diff.recycle()

        return result
    }

    private fun createDiffImage(expected: Bitmap, actual: Bitmap): Bitmap {
        val width = expected.width
        val height = expected.height

        val diff = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val expectedPixels = IntArray(width * height)
        val actualPixels = IntArray(width * height)

        expected.getPixels(expectedPixels, 0, width, 0, 0, width, height)
        actual.getPixels(actualPixels, 0, width, 0, 0, width, height)

        val diffPixels = IntArray(width * height)

        for (i in expectedPixels.indices) {
            diffPixels[i] = if (!pixelsEqual(expectedPixels[i], actualPixels[i], 0)) {
                Color.RED
            } else {
                Color.TRANSPARENT
            }
        }

        diff.setPixels(diffPixels, 0, width, 0, 0, width, height)
        return diff
    }
}

fun mkDir(dir: String): Boolean {
    val file = File(dir)
    return file.exists() || file.mkdirs()
}
