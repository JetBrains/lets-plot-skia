/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.defaultViewer

import org.jetbrains.letsPlot.swing.skia.PlotViewerWindowSkia
import plotSpec.DensitySpec

fun main() {
    with(DensitySpec()) {
        PlotViewerWindowSkia(
            "Density plot",
            createFigure(),
            null,
            preserveAspectRatio = false
        ).open()
    }
}
