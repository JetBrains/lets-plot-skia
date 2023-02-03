package me.ikupriyanov.demo.plot

import jetbrains.datalore.plotDemo.model.plotConfig.TooltipAnchor
import me.ikupriyanov.demo.utils.PlotWindowSkia

fun main() {
    PlotWindowSkia(
        "Tooltip Anchor",
        TooltipAnchor().plotSpecList()
    ).open()
}
