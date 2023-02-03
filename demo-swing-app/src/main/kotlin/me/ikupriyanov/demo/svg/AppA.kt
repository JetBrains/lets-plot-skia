package me.ikupriyanov.demo.svg

import jetbrains.datalore.vis.svgMapper.common.DemoModelA
import me.ikupriyanov.demo.utils.SvgWindowSkia

fun main() {
    SvgWindowSkia( "SwingSkia DemoA", listOf(DemoModelA.createModel()))
}
