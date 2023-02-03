package me.ikupriyanov.demo.plot

import jetbrains.datalore.plotDemo.model.plotConfig.MarginalLayersFacetsDemo
import me.ikupriyanov.demo.utils.PlotWindowSkia

object MarginalLayersFacetsViewerSkia {
    @JvmStatic
    fun main(args: Array<String>) {
        PlotWindowSkia(
            "Marginal layers with facets",
            MarginalLayersFacetsDemo().plotSpecList(),
        ).open()
    }
}
