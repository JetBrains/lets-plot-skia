package me.ikupriyanov.demo.plot

import jetbrains.datalore.plotDemo.model.plotConfig.FacetWrapDemo
import me.ikupriyanov.demo.utils.PlotWindowSkia

object FacetWrapViewerSkia {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotWindowSkia(
            "Facet wrap (Skia)",
            FacetWrapDemo().plotSpecList(),
        ).open()
    }
}
