package jetbrains.datalore.vis.svgMapper.skia

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.plot.server.config.BackendSpecTransformUtil

object MonolithicSkia {
    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): Result<List<MonolithicCommon.PlotBuildInfo>> {
        try {
            @Suppress("NAME_SHADOWING")
            val plotSpec = processSpecs(plotSpec, frontendOnly = false)
            return buildPlotFromProcessedSpecs(
                plotSpec,
                plotSize,
                plotMaxWidth,
                computationMessagesHandler
            )
        } catch (e: RuntimeException) {
            return Result.failure(e)
        }
    }

    fun buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): Result<List<MonolithicCommon.PlotBuildInfo>> {
        try {
            val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
                plotSpec,
                plotSize,
                plotMaxWidth,
                plotPreferredWidth = null
            )
            if (buildResult is MonolithicCommon.PlotsBuildResult.Error) {
                return Result.failure(Throwable(buildResult.error))
            }

            val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
            val computationMessages = success.buildInfos.flatMap { it.computationMessages }
            computationMessagesHandler(computationMessages)
            return Result.success(success.buildInfos)
        } catch (e: RuntimeException) {
            return Result.failure(e)
        }
    }

    @Suppress("SameParameterValue")
    private fun processSpecs(plotSpec: MutableMap<String, Any>, frontendOnly: Boolean): MutableMap<String, Any> {
        PlotConfig.assertPlotSpecOrErrorMessage(plotSpec)
        if (PlotConfig.isFailure(plotSpec)) {
            return plotSpec
        }

        // Backend transforms
        @Suppress("NAME_SHADOWING")
        val plotSpec =
            if (frontendOnly) {
                plotSpec
            } else {
                // This transform doesn't need to be "portable"
                // Could use PlotConfigServerSideJvm in case we needed "encoding"
                BackendSpecTransformUtil.processTransform(plotSpec)
            }

        if (PlotConfig.isFailure(plotSpec)) {
            return plotSpec
        }

        // Frontend transforms
        return PlotConfigClientSide.processTransform(plotSpec)
    }
}