/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.defaultViewer

import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skiko.swing.PlotViewerWindowSkiko
import plotSpec.DensitySpec

fun main() {
    with(DensitySpec()) {
        PlotViewerWindowSkiko(
            "Density plot",
            null,
            createFigure().toSpec(),
            preserveAspectRatio = false
        ).open()
    }
}
