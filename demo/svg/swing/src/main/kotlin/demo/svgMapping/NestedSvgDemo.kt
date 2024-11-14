/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping

import demo.svgMapping.utils.DemoWindow
import demo.svgModel.NestedSvgModel

fun main() {
    DemoWindow("Nested SVG", listOf(NestedSvgModel.m())).open()
}
