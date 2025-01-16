/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package plotSpec

import demoData.AutoMpg
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.tooltips.layerTooltips

class ScatterSpec : PlotDemoSpec {
    override fun createFigureList(): List<Figure> {
        return listOf(
            scatter()
        )
    }

    fun scatter(): Figure {
        return letsPlot(AutoMpg.map()) + geomPoint(
            tooltips = layerTooltips().anchor("top_center").line("Origin: @{vehicle name}")
        ) {
            x = "engine horsepower"
            y = "miles per gallon"
            color = "origin of car"
        }
    }
}