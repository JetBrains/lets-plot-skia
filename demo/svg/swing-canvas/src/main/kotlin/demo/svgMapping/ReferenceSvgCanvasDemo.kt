/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping

import demo.svgMapping.utils.CanvasDemoWindow
import demo.svgModel.ReferenceSvgModel

fun main() {
    CanvasDemoWindow("Reference SVG", listOf(ReferenceSvgModel.createModel())).open()
}
