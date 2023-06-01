package demo.svgMapping

import demo.svgMapping.utils.DemoWindow
import svgModel.DemoModelA

fun main() {
    DemoWindow("SwingSkia DemoA", listOf(DemoModelA.createModel())).open()
}
