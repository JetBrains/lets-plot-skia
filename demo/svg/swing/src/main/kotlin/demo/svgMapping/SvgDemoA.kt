/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping

import demo.svgMapping.utils.DemoWindow
import svgModel.DemoModelA

fun main() {
    DemoWindow("SwingSkia DemoA", listOf(DemoModelA.createModel())).open()
}
