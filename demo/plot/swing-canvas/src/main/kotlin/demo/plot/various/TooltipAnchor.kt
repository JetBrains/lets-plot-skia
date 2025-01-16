/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.various

import demo.util.PlotSpecsCanvasDemoWindow
import plotSpec.TooltipAnchorSpec

fun main() {
    with(TooltipAnchorSpec()) {
        PlotSpecsCanvasDemoWindow(
            "Tooltip Anchor",
            createFigureList(),
        ).open()
    }
}
