/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.awt.builderHW

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.spec.FailureHandler
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import javax.swing.JComponent
import javax.swing.JTextArea

/**
 * "heavyweight" - one JComponent is built per plot spec.
 */
object MonolithicSkiaAwt {

    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        containerSize: DoubleVector?,
        sizingPolicy: SizingPolicy,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return try {
            @Suppress("NAME_SHADOWING")
            val plotSpec = MonolithicCommon.processRawSpecs(plotSpec, frontendOnly = false)
            buildPlotFromProcessedSpecs(plotSpec, containerSize, sizingPolicy, computationMessagesHandler)
        } catch (e: RuntimeException) {
            handleException(e)
        }
    }

    fun buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        containerSize: DoubleVector?,
        sizingPolicy: SizingPolicy,
        computationMessagesHandler: (List<String>) -> Unit
    ): JComponent {
        val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
            plotSpec,
            containerSize,
            sizingPolicy,
        )

        if (buildResult.isError) {
            val errorMessage =
                (buildResult as MonolithicCommon.PlotsBuildResult.Error).error
            return createErrorLabel(errorMessage)
        }

        val success = buildResult as MonolithicCommon.PlotsBuildResult.Success
        computationMessagesHandler(success.buildInfo.computationMessages)

        return FigureToSkiaAwt(success.buildInfo).eval()
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
