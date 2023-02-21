package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.builder.FigureBuildInfo
import jetbrains.datalore.plot.config.FailureHandler

internal object MonolithicAndroid {
    fun Context.buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): View {
        return try {
            @Suppress("NAME_SHADOWING")
            val plotSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)
            buildPlotFromProcessedSpecs(plotSpec, plotSize, plotMaxWidth, computationMessagesHandler)
        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    fun Context.buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): View {
        return try {
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
                // a single plot
                val buildInfo = success.buildInfos[0]
                val plotView = FigureToSkia(buildInfo, plotSize, plotMaxWidth).eval(this)
                LinearLayout(this).apply {
                    addView(plotView)
                }
            } else {
                // ggbunch
                buildGGBunchComponent(success.buildInfos)
            }

        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    private fun Context.buildGGBunchComponent(plotInfos: List<FigureBuildInfo>): View {
        error("Not implemented")
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
