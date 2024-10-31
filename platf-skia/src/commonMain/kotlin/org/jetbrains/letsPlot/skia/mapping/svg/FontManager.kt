/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.mapping.svg

import org.jetbrains.skia.Font
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.FontStyle
import org.jetbrains.skia.Typeface

class FontManager {
    fun matchFamiliesStyle(fontFamily: List<String>, fontStyle: FontStyle): Typeface {
        val fontConfig = fontFamily to fontStyle

        if (fontConfig in typefaceCache) {
            return typefaceCache.getValue(fontConfig)
        }

        var typeface = FontMgr.default.matchFamiliesStyle(fontFamily.toTypedArray(), fontStyle)
        if (typeface == null || typeface.familyName == "") {
            typeface = FontMgr.default.matchFamilyStyle("sans-serif", fontStyle)
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

    private val typefaceCache = mutableMapOf<Pair<List<String>, FontStyle>, Typeface>()
    private val fontCache = mutableMapOf<Pair<Typeface, Float>, Font>()
}
