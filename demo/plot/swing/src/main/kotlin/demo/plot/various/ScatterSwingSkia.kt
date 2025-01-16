/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import plotSpec.ScatterSpec

fun main() {
    with(ScatterSpec()) {
        PlotSpecsDemoWindow(
            "Scatter-plot",
            createFigureList(),
        ).open()
    }
}
