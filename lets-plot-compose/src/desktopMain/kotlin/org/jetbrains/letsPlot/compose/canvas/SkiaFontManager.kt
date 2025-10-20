/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.compose.canvas

import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.skia.*

class SkiaFontManager {
    fun matchFamiliesStyle(fontFamily: List<String>, fontStyle: FontStyle): Typeface {
        val fontConfig = fontFamily to fontStyle

        if (fontConfig in typefaceCache) {
            return typefaceCache.getValue(fontConfig)
        }

        var typeface = fontFamily.firstNotNullOfOrNull {
            FontMgr.default.matchFamilyStyle(it, fontStyle)
        }

        if (typeface == null || typeface.familyName == "") {
            typeface = fontFamily.firstNotNullOfOrNull {
                FontMgr.default.legacyMakeTypeface(it, fontStyle)
            }
        }

        if (typeface == null || typeface.familyName == "") {
            typeface = FontMgr.default.legacyMakeTypeface("sans-serif", fontStyle)
        }

        if (typeface == null || typeface.familyName == "") {
            println("Font not found: [${fontFamily.joinToString()}]")
            typeface = Typeface.makeEmpty()
        }

        typefaceCache[fontConfig] = typeface
        return typeface
    }

    fun font(typeface: Typeface, fontSize: Float): Font {
        return fontCache
            .getOrPut(typeface to fontSize) {
                Font(typeface, fontSize).apply {
                    isSubpixel = true
                }
            }
    }

    fun dispose() {
        typefaceCache.values.forEach(Typeface::close)
        typefaceCache.clear()
        fontCache.values.forEach(Font::close)
        fontCache.clear()
    }

    fun findFont(f: org.jetbrains.letsPlot.core.canvas.Font): Font {
        val fontStyle = FontStyle(
            when (f.fontWeight) {
                FontWeight.NORMAL -> org.jetbrains.skia.FontWeight.NORMAL
                FontWeight.BOLD -> org.jetbrains.skia.FontWeight.BOLD
            },
            FontWidth.NORMAL,
            FontSlant.UPRIGHT
        )

        val typeface =  matchFamiliesStyle(listOf(f.fontFamily), fontStyle)
        return font(typeface, f.fontSize.toFloat())
    }

    private val typefaceCache = mutableMapOf<Pair<List<String>, FontStyle>, Typeface>()
    private val fontCache = mutableMapOf<Pair<Typeface, Float>, Font>()
}
