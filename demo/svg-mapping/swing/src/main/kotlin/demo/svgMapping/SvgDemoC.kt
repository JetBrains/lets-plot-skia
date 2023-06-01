package demo.svgMapping

import demo.svgMapping.utils.DemoWindow
import svgModel.DemoModelC

fun main() {
    DemoWindow("SwingSkia DemoC", listOf(DemoModelC.createModel())).open()
}
