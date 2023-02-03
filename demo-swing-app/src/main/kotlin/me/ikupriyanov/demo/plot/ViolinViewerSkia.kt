package me.ikupriyanov.demo.plot

import jetbrains.datalore.plotDemo.model.plotConfig.Violin
import me.ikupriyanov.demo.utils.PlotWindowSkia

object ViolinViewerSkia {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotWindowSkia(
            "Violin plot",
            Violin().plotSpecList(),
        ).open()
    }
}
