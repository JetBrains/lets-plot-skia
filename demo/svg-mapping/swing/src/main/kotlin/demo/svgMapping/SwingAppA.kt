package demo.svgMapping

import demo.svgMapping.utils.SvgWindowSkia
import svgModel.DemoModelA

fun main() {
    SvgWindowSkia("SwingSkia DemoA", listOf(DemoModelA.createModel())).open()
}
