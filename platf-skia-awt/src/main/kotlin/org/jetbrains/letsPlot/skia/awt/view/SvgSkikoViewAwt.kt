/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.awt.view

import org.jetbrains.letsPlot.skia.view.SvgSkikoView
import org.jetbrains.skiko.SkiaLayer
import java.awt.Dimension

internal class SvgSkikoViewAwt : SvgSkikoView() {
    override fun updateSkiaLayerSize(width: Int, height: Int) {
        skiaLayer.preferredSize = Dimension(width, height)
    }

    override fun createSkiaLayer(view: SvgSkikoView): SkiaLayer {
        return SkiaLayer().also {
            // https://github.com/JetBrains/skiko/issues/614
            //skiaLayer.skikoView = skikoView
            it.addView(view)
        }
    }
}