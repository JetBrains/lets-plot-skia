package me.ikupriyanov.demo.plot

import jetbrains.datalore.plotDemo.model.plotConfig.Raster
import me.ikupriyanov.demo.utils.PlotWindowSkia

object RasterViewerSkia {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotWindowSkia(
            "geom_raster",
            Raster().plotSpecList(),
        ).open()
    }
}
