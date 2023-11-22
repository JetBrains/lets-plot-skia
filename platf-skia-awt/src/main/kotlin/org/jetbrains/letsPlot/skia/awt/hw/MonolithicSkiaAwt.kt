/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.awt.hw

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.spec.FailureHandler
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import javax.swing.JComponent
import javax.swing.JTextArea

/**
 * "heavyweight" - one JComponent is built per plot spec.
 */
object MonolithicSkiaAwt {

    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return try {
            @Suppress("NAME_SHADOWING")
            val plotSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)
            buildPlotFromProcessedSpecs(plotSpec, plotSize, computationMessagesHandler)
        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    fun buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        computationMessagesHandler: (List<String>) -> Unit
    ): JComponent {
        val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
            plotSpec,
            plotSize,
            plotMaxWidth = null,
            plotPreferredWidth = null
        )

        if (buildResult.isError) {
            val errorMessage =
                (buildResult as MonolithicCommon.PlotsBuildResult.Error).error
            return createErrorLabel(errorMessage)
        }

        val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
        val computationMessages = success.buildInfos.flatMap { it.computationMessages }
        computationMessagesHandler(computationMessages)

        return if (success.buildInfos.size == 1) {
            val buildInfo = success.buildInfos.single()
            FigureToSkiaAwt(buildInfo).eval()
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
