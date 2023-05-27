package demo.svgMapping

import demo.svgMapping.utils.SvgWindowSkia
import svgModel.DemoModelB

fun main() {
    SvgWindowSkia("SwingSkia DemoB", listOf(DemoModelB.createModel())).open()
}
