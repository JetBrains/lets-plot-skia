/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import org.jetbrains.letsPlot.android.canvas.CanvasView
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.util.MonolithicCommon.processRawSpecs
import org.jetbrains.letsPlot.core.util.PlotThemeHelper
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.raster.view.PlotCanvasFigure

//import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger

//private val LOG = NaiveLogger("PlotPanel")
private val LOG = PortableLogging.logger(name = "[PlotPanelRaw]")

@Suppress("FunctionName")
@Composable
actual fun PlotPanelRaw(
    rawSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean,
    modifier: Modifier,
    errorTextStyle: TextStyle,
    errorModifier: Modifier,
    computationMessagesHandler: (List<String>) -> Unit
) {
    var plotCanvasFigure by remember { mutableStateOf(PlotCanvasFigure()) }

    // Cache processed plot spec to avoid reprocessing the same raw spec on every recomposition.

    // Note: Use remember(rawSpec.hashCode()), to bypass the equality check and use the content hash directly.
    // The issue was that remember(rawSpec) uses some kind of comparison (equals()?) which somehow not working for `MutableMap`.
    val processedPlotSpec = remember(rawSpec.hashCode()) {
        processRawSpecs(rawSpec, frontendOnly = false)
    }

    val showErrorMessage = PlotConfig.isFailure(processedPlotSpec)

    println("PlotPanel: recomposition")

    // Background
    val finalModifier = if (showErrorMessage) {
        modifier.background(Color.LightGray)
    } else {
        if (containsBackground(modifier)) {
            // Do not change the user-defined background
            modifier
        } else {
            // Use background color from the plot theme
            val lpColor = PlotThemeHelper.plotBackground(processedPlotSpec)
            val lpBackground = Color(lpColor.red, lpColor.green, lpColor.blue, lpColor.alpha)
            modifier.background(lpBackground)
        }
    }

//    LOG.info { "Recompose PlotPanel()" }
    if (showErrorMessage) {
        // Reset the figure to resolve the 'Registration already removed' error.
        // On error, the CanvasView is removed and the plotCanvasFigure changes state to 'detached',
        // meaning it cannot be reused.
        @Suppress("AssignedValueIsNeverRead")  // false positive? The variable is used in AndroidView below.
        plotCanvasFigure = PlotCanvasFigure()

        // Show error message
        BasicTextField(
            value = PlotConfig.getErrorMessage(processedPlotSpec),
            onValueChange = { },
            readOnly = true,
            textStyle = errorTextStyle,
            modifier = errorModifier
        )
    } else {
        @Suppress("COMPOSE_APPLIER_CALL_MISMATCH") // Gemini says that this is a false positive
        AndroidView(
            modifier = finalModifier,
            factory = { ctx ->
                CanvasView(ctx).apply {
                    figure = plotCanvasFigure
                }
            },
            update = { _ ->
                plotCanvasFigure.update(
                    processedPlotSpec,
                    SizingPolicy.fitContainerSize(preserveAspectRatio),
                    computationMessagesHandler
                )
            }
        )
    }
}

private fun containsBackground(modifier: Modifier): Boolean {
    return modifier.foldIn(false) { hasBg, element ->
        hasBg || element.toString().contains("BackgroundElement")
    }
}
