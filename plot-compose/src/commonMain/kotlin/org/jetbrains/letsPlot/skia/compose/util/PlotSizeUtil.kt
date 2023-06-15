package org.jetbrains.letsPlot.skia.compose.util

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.PlotSizeHelper
import jetbrains.datalore.plot.config.CompositeFigureConfig
import jetbrains.datalore.plot.config.FigKind
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.PlotConfigClientSide

/**
 * ToDo: temporary here. This service must be provided be core LP.
 *
 * All code was copied from: PlotSpecComponentProvider (jetbrains.datalore.vis.swing)
 */
internal object PlotSizeUtil {

    fun preferredFigureSize(
        figureSpec: Map<String, Any>,
        preserveAspectRatio: Boolean,
        containerSize: DoubleVector
    ): DoubleVector {

        if (PlotConfig.isFailure(figureSpec)) {
            // just keep given size
            return containerSize
        }

        return when (PlotConfig.figSpecKind(figureSpec)) {

            FigKind.GG_BUNCH_SPEC -> {
                // Don't scale GGBunch.
                val bunchSize = PlotSizeHelper.plotBunchSize(figureSpec)
                DoubleVector(bunchSize.x, bunchSize.y)
            }

            FigKind.SUBPLOTS_SPEC -> {
                // Subplots figure has flexible size.
                if (!preserveAspectRatio) {
                    return containerSize
                }

                val compositeFigureConfig = CompositeFigureConfig(figureSpec) {
                    // ignore message when computing a figure size.
                }

                val defaultSize = PlotSizeHelper.compositeFigureSize(
                    compositeFigureConfig,
                    plotSize = null,
                    plotMaxWidth = null,
                    plotPreferredWidth = null,
                )
                fitPlotInContainer(plotSize = defaultSize, containerSize)
            }

            FigKind.PLOT_SPEC -> {
                // Singe plot has flexible size.
                if (!preserveAspectRatio) {
                    return containerSize
                }

                val config = PlotConfigClientSide.create(figureSpec) { /*ignore messages*/ }
                val defaultSize = PlotSizeHelper.singlePlotSize(
                    figureSpec,
                    plotSize = null,
                    plotMaxWidth = null,
                    plotPreferredWidth = null,
                    config.facets,
                    config.containsLiveMap
                )

                fitPlotInContainer(plotSize = defaultSize, containerSize)
            }
        }
    }

    private fun fitPlotInContainer(plotSize: DoubleVector, containerSize: DoubleVector): DoubleVector {
        val aspectRatio = plotSize.x / plotSize.y

        val width = containerSize.x
        val height = containerSize.y

        return if (aspectRatio >= 1.0) {
            val plotHeight = width / aspectRatio
            val scaling = if (plotHeight > height) height / plotHeight else 1.0
            DoubleVector(width * scaling, plotHeight * scaling)
        } else {
            val plotWidth = height * aspectRatio
            val scaling = if (plotWidth > width) width / plotWidth else 1.0
            DoubleVector(plotWidth * scaling, height * scaling)
        }
    }
}