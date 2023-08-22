/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import plotSpec.ViolinSpec

fun main() {
    with(ViolinSpec()) {
        PlotSpecsDemoWindow(
            "Violin",
            createFigureList(),
        ).open()
    }
}
