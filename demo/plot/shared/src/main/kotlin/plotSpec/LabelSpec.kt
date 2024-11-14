/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package plotSpec

import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.geom.geomLabel
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.themes.themeVoid

class LabelSpec : PlotDemoSpec {
    override fun createFigureList(): List<Figure> {
        val fonts = run {
            val families = listOf(
                "monospace"
            )
            letsPlot() + geomLabel {
                y = families.indices
                label = families
                family = families
            } + themeVoid()
        }

        return listOf(fonts)
    }
}