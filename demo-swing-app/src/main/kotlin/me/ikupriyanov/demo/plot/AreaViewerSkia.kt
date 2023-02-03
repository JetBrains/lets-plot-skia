package me.ikupriyanov.demo.plot

import jetbrains.datalore.plotDemo.model.plotConfig.Area
import me.ikupriyanov.demo.utils.PlotWindowSkia

object AreaViewerSkia {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotWindowSkia(
            "Area plot",
            Area().plotSpecList(),
        ).open()
    }
}
