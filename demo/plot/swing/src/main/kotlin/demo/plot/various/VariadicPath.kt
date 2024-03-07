/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.various

import demo.util.PlotSpecsDemoWindow
import plotSpec.VariadicPathSpec

fun main() {
    with(VariadicPathSpec()) {
        PlotSpecsDemoWindow(
            "Variadic Path",
            createFigureList(),
        ).open()
    }
}
