/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svg

import demo.svg.utils.DemoWindow
import demo.svgModel.SvgImageElementModel

fun main() {
    DemoWindow("SvgImage demo", listOf(SvgImageElementModel.createModel())).open()
}
