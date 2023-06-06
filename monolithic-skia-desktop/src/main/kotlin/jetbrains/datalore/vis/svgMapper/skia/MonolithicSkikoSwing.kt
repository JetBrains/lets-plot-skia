package jetbrains.datalore.vis.svgMapper.skia

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.config.FailureHandler
import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextArea
import kotlin.math.ceil

object MonolithicSkikoSwing {
    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return try {
            @Suppress("NAME_SHADOWING")
            val plotSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)
            buildPlotFromProcessedSpecs(plotSpec, plotSize, plotMaxWidth, computationMessagesHandler)
        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    fun buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        computationMessagesHandler: (List<String>) -> Unit
    ): JComponent {
        val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
            plotSpec,
            plotSize,
            plotMaxWidth,
            plotPreferredWidth = null
        )

        if (buildResult.isError) {
            val errorMessage = (buildResult as MonolithicCommon.PlotsBuildResult.Error).error
            return createErrorLabel(errorMessage)
        }

        val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
        val computationMessages = success.buildInfos.flatMap { it.computationMessages }
        computationMessagesHandler(computationMessages)

        return if (success.buildInfos.size == 1) {
            val buildInfo = success.buildInfos.single()
            FigureToSkikoSwing(buildInfo).eval()
        } else {
            return buildGGBunchComponent(success)
        }
    }

    private fun buildGGBunchComponent(success: MonolithicCommon.PlotsBuildResult.Success): JPanel {
        val container = JPanel(null)
        success.buildInfos.forEach { plotBuildInfo ->
            val plotComponent = FigureToSkikoSwing(plotBuildInfo).eval()

            val bounds = plotBuildInfo.bounds
            val boundsAwt = Rectangle(
                bounds.origin.x.toInt(),
                bounds.origin.y.toInt(),
                ceil(bounds.dimension.x).toInt(),
                ceil(bounds.dimension.y).toInt()
            )

            plotComponent.bounds = boundsAwt // If no layout in parent component
            plotComponent.minimumSize = boundsAwt.size // .. if otherwise ...
            plotComponent.preferredSize = boundsAwt.size
            container.add(plotComponent)
        }

        val bunchBounds = success.buildInfos.map { it.bounds }
            .fold(DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)) { acc, bounds ->
                acc.union(bounds)
            }

        val bunchDimensions = Dimension(
            bunchBounds.width.toInt(),
            bunchBounds.height.toInt()
        )

        container.preferredSize = bunchDimensions
        container.minimumSize = bunchDimensions
        container.maximumSize = bunchDimensions
        return container
    }

    private fun handleException(e: RuntimeException): JComponent {
        val failureInfo = FailureHandler.failureInfo(e)
        if (failureInfo.isInternalError) {
            println(e)
        }
        return createErrorLabel(failureInfo.message)
    }


    private fun createErrorLabel(s: String): JComponent {
        return JTextArea(s)
    }
}
