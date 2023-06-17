package org.jetbrains.letsPlot.skia.android

import android.content.Context
import android.view.View
import android.widget.TextView
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.config.FailureHandler

object MonolithicSkiaAndroid {
    fun buildPlotFromRawSpecs(
        ctx: Context,
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): View {
        return try {
            val processedPlotSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)
            return buildPlotFromProcessedSpecs(ctx, processedPlotSpec, plotSize, computationMessagesHandler)
        } catch (e: RuntimeException) {
            ctx.handleException(e)
        }
    }

    fun buildPlotFromProcessedSpecs(
        ctx: Context,
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): View {
        return try {
            val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
                plotSpec,
                plotSize,
                plotMaxWidth = null,
                plotPreferredWidth = null
            )
            if (buildResult.isError) {
                val errorMessage = (buildResult as MonolithicCommon.PlotsBuildResult.Error).error
                return ctx.createErrorLabel(errorMessage)
            }

            val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
            val computationMessages = success.buildInfos.flatMap { it.computationMessages }
            computationMessagesHandler(computationMessages)
            return if (success.buildInfos.size == 1) {
                // a single plot
                val buildInfo = success.buildInfos[0]
                FigureToAndroid(buildInfo).eval(ctx)
            } else {
                // ggbunch
                error("GGBunch is not supported.")
            }

        } catch (e: RuntimeException) {
            ctx.handleException(e)
        }
    }

    private fun Context.handleException(e: RuntimeException): View {
        val failureInfo = FailureHandler.failureInfo(e)
        if (failureInfo.isInternalError) {
            println(e)
        }
        return createErrorLabel(failureInfo.message)
    }

    private fun Context.createErrorLabel(s: String): View {
        val label = TextView(this)
        label.text = s
        label.setTextColor(0x00FF0000)
        return label
    }
}