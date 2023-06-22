package org.jetbrains.letsPlot.skia.awt

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.config.FailureHandler
import javax.swing.JComponent
import javax.swing.JTextArea

object MonolithicSkiaAwt {
    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        isComposeDesktop: Boolean,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return try {
            @Suppress("NAME_SHADOWING")
            val plotSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)
            buildPlotFromProcessedSpecs(plotSpec, plotSize, isComposeDesktop, computationMessagesHandler)
        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    fun buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        isComposeDesktop: Boolean,
        computationMessagesHandler: (List<String>) -> Unit
    ): JComponent {
        val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
            plotSpec,
            plotSize,
            plotMaxWidth = null,
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
            FigureToAwt(buildInfo, isComposeDesktop).eval()
        } else {
            throw IllegalArgumentException("GGBunch is not supported.")
        }
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
