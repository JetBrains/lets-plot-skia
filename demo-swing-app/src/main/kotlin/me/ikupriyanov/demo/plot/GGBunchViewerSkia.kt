package me.ikupriyanov.demo.plot

import jetbrains.datalore.plotDemo.model.plotConfig.GGBunch
import me.ikupriyanov.demo.utils.PlotWindowSkia

object GGBunchViewerSkia {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotWindowSkia(
            "GGBunch plot",
            GGBunch().plotSpecList(),
        ).open()
    }
}
