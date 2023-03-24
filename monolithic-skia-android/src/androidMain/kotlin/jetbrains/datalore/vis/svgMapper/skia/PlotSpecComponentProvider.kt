package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.view.View
import android.widget.ScrollView
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.PlotSizeHelper
import jetbrains.datalore.plot.config.FigKind
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.vis.svgMapper.skia.MonolithicAndroid.buildPlotFromProcessedSpecs
import kotlin.math.ceil
import kotlin.math.floor

internal class PlotSpecComponentProvider(
    private val processedSpec: MutableMap<String, Any>,
    private val preserveAspectRatio: Boolean,
    private val computationMessagesHandler: (List<String>) -> Unit
) : PlotComponentProvider {

    override fun getPreferredSize(containerSize: DoubleVector): DoubleVector {
        return preferredFigureSize(processedSpec, preserveAspectRatio, containerSize)
    }

    override fun createComponent(ctx: Context, containerSize: DoubleVector?): View {
        val plotSize = containerSize?.let(::getPreferredSize)

        val plotComponent = ctx.buildPlotFromProcessedSpecs(
            plotSize = plotSize,
            plotSpec = processedSpec,
            plotMaxWidth = null,
            computationMessagesHandler = computationMessagesHandler
        )

        val isGGBunch =
            !PlotConfig.isFailure(processedSpec) && PlotConfig.figSpecKind(processedSpec) == FigKind.GG_BUNCH_SPEC
        return if (isGGBunch) {
            // GGBunch is always 'original' size => add a scroll pane.
            val scrollPane = ScrollView(plotComponent.context)
            //containerSize?.run {
            //    scrollPane.preferredSize = containerSize
            //    scrollPane.size = containerSize
            //}
            scrollPane
        } else {
            plotComponent
        }
    }

    companion object {

        private fun preferredFigureSize(
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
                    DoubleVector(ceil(bunchSize.x), ceil(bunchSize.y))
                }

                FigKind.SUBPLOTS_SPEC -> {
                    // Subplots figure has flexible size.
                    if (!preserveAspectRatio) {
                        return containerSize
                    }

                    val defaultSize = PlotSizeHelper.subPlotsSize(
                        figureSpec,
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
                DoubleVector(floor(width * scaling), floor(plotHeight * scaling))
            } else {
                val plotWidth = height * aspectRatio
                val scaling = if (plotWidth > width) width / plotWidth else 1.0
                DoubleVector(floor(plotWidth * scaling), floor(height * scaling))
            }
        }
    }
}