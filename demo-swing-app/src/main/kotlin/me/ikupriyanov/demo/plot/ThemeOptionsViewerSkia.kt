package me.ikupriyanov.demo.plot

import jetbrains.datalore.plotDemo.model.plotConfig.ThemeOptions
import me.ikupriyanov.demo.utils.PlotWindowSkia

object ThemeOptionsViewerSkia {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotWindowSkia(
            "Theme options",
            ThemeOptions().plotSpecList(),
        ).open()
    }
}
