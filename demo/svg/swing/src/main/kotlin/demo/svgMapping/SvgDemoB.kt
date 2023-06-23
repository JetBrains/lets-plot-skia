package demo.svgMapping

import demo.svgMapping.utils.DemoWindow
import svgModel.DemoModelB

fun main() {
    DemoWindow("SwingSkia DemoB", listOf(DemoModelB.createModel())).open()
}
