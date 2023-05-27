package demo.svgMapping

import demo.svgMapping.utils.SvgWindowSkia
import svgModel.DemoModelC

fun main() {
    SvgWindowSkia("SwingSkia DemoC", listOf(DemoModelC.createModel())).open()
}
