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
        return typefaces.getOrPut(fontFamily to fontStyle) {
            FontMgr.default.matchFamiliesStyle(fontFamily.toTypedArray(), fontStyle) ?: Typeface.makeEmpty()
        }
    }

    fun font(typeface: Typeface, fontSize: Float): Font {
        return fonts.getOrPut(typeface to fontSize) {
            Font(typeface, fontSize).apply { isSubpixel = true }
        }
    }

    fun dispose() {
        typefaces.values.forEach(Typeface::close)
        typefaces.clear()
        fonts.values.forEach(Font::close)
        fonts.clear()
    }

    private val typefaces = mutableMapOf<Pair<List<String>, FontStyle>, Typeface>()
    private val fonts = mutableMapOf<Pair<Typeface, Float>, Font>()
}