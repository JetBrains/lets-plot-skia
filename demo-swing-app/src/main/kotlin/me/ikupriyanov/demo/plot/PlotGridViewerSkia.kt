package me.ikupriyanov.demo.plot

import jetbrains.datalore.plotDemo.model.plotConfig.PlotGrid
import me.ikupriyanov.demo.utils.PlotWindowSkia

object PlotGridViewerSkia {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotWindowSkia(
            "Plot Grid (Skia)",
            PlotGrid().plotSpecList(),
        ).open()
    }
}
