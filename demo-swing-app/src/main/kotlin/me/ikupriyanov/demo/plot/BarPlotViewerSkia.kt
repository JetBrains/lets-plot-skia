package me.ikupriyanov.demo.plot

import jetbrains.datalore.plotDemo.model.plotConfig.BarPlot
import me.ikupriyanov.demo.utils.PlotWindowSkia

object BarPlotViewerSkia {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotWindowSkia(
            "Bar plot",
            BarPlot().plotSpecList(),
        ).open()
    }
}
