/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.compose.canvas.SkiaCanvasPeer
import org.jetbrains.letsPlot.compose.canvas.SkiaContext2d
import org.jetbrains.letsPlot.compose.canvas.SkiaFontManager
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.front.SpecOverrideUtil.applySpecOverride
import org.jetbrains.letsPlot.core.util.MonolithicCommon.processRawSpecs
import org.jetbrains.letsPlot.core.util.PlotThemeHelper
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.raster.view.PlotCanvasFigure2
import java.awt.Cursor

//import org.jetbrains.letsPlot.compose.util.NaiveLogger

//private val LOG = NaiveLogger("PlotPanel")
private val LOG = PortableLogging.logger(name = "[PlotPanelRaw]")

private const val logRecompositions = false

@Suppress("FunctionName")
@Composable
fun PlotPanelRaw2(
    rawSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean,
    modifier: Modifier,
    errorTextStyle: TextStyle = TextStyle(color = Color(0xFF700000)),
    errorModifier: Modifier = Modifier.padding(16.dp),
    computationMessagesHandler: (List<String>) -> Unit
) {
    if (logRecompositions) {
        println("PlotPanelRaw: recomposition")
    }

    val composeMouseEventMapper = remember { ComposeMouseEventMapper() }
    // Update density on each recomposition to handle monitor DPI changes (e.g., drag between HIDPI/regular monitor)
    val density = LocalDensity.current.density.toDouble()

    // Cache processed plot spec to avoid reprocessing the same raw spec on every recomposition.

    // Note: Use remember(rawSpec.hashCode()), to bypass the equality check and use the content hash directly.
    // The issue was that remember(rawSpec) uses some kind of comparison (equals()?) which somehow not working for `MutableMap`.
    val processedPlotSpec = remember(rawSpec.hashCode()) {
        processRawSpecs(rawSpec, frontendOnly = false)
    }

    var panelSize by remember { mutableStateOf(DoubleVector.ZERO) }
    var dispatchComputationMessages by remember { mutableStateOf(true) }
    var specOverrideList by remember { mutableStateOf(emptyList<Map<String, Any>>()) }

    //var plotFigureModel by remember { mutableStateOf<PlotFigureModel?>(null) }


    var errorMessage: String? by remember(processedPlotSpec) { mutableStateOf(null) }

    // Reset the old plot on error to prevent blinking
    // We can't reset PlotContainer using updateViewmodel(), so we create a new one.
    val skiaCanvasPeer = SkiaCanvasPeer()
    val plotCanvasFigure2 = remember(errorMessage) {
        PlotCanvasFigure2().apply {
            mapToCanvas(skiaCanvasPeer)
        }
    }

    // Background
    val finalModifier = if (errorMessage != null) {
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


    DisposableEffect(plotCanvasFigure2) {
        onDispose {
            // Try/catch to ensure that any exception in dispose() does not break the Composable lifecycle
            // Otherwise, the app window gets unclosable.
            try {
                //plotCanvasFigure2.dispose()
            } catch (e: Exception) {
                LOG.error(e) { "PlotContainer.dispose() failed" }
            }
        }
    }

    Column(modifier = finalModifier) {
        //if (plotFigureModel != null && GG_TOOLBAR in processedPlotSpec) {
        //    PlotToolbar(plotFigureModel!!)
        //}

        Box(
            modifier = finalModifier
                .weight(1f) // Take the remaining vertical space
                .fillMaxWidth() // Fill available width
                .onSizeChanged { newSize ->
                    // Convert logical pixels (from Compose layout) to physical pixels (plot SVG pixels)
                    panelSize = DoubleVector(newSize.width / density, newSize.height / density)
                }
        ) {
            val errMsg = errorMessage
            if (errMsg != null) {
                // Show error message
                BasicTextField(
                    value = errMsg,
                    onValueChange = { },
                    readOnly = true,
                    textStyle = errorTextStyle,
                    modifier = errorModifier
                )
            } else {
                // Render the plot
                LaunchedEffect(panelSize, processedPlotSpec, specOverrideList, preserveAspectRatio) {

                    if (PlotConfig.isFailure(processedPlotSpec)) {
                        errorMessage = PlotConfig.getErrorMessage(processedPlotSpec)
                        return@LaunchedEffect
                    }

                    runCatching {
                        if (panelSize != DoubleVector.ZERO) {
                            val plotSpec = applySpecOverride(processedPlotSpec, specOverrideList).toMutableMap()

                            plotCanvasFigure2.update(plotSpec, SizingPolicy.fitContainerSize(preserveAspectRatio)) { messages ->
                                if (dispatchComputationMessages) {
                                    // do once
                                    dispatchComputationMessages = false
                                    computationMessagesHandler(messages)
                                }
                            }

                            val plotWidth = plotCanvasFigure2.size.x
                            val plotHeight = plotCanvasFigure2.size.y

                            // Calculate centering position in physical pixels
                            // Both panelSize and plot dimensions are in physical pixels
                            val position = DoubleVector(
                                maxOf(0.0, (panelSize.x - plotWidth) / 2.0),
                                maxOf(0.0, (panelSize.y - plotHeight) / 2.0)
                            )
                        }
                    }.getOrElse { e ->
                        errorMessage = "${e.message}"
                        return@LaunchedEffect
                    }
                }

                Canvas(
                    modifier = modifier
                        .fillMaxSize()
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.CROSSHAIR_CURSOR)))
                        .onSizeChanged { size ->
                            // Convert canvas logical pixels (from Compose layout) to physical pixels (plot SVG pixels)
                            val width = (size.width / density).toInt()
                            val height = (size.height / density).toInt()
                            plotCanvasFigure2.resize(width, height)
                        }
                        .pointerInput(composeMouseEventMapper, composeMouseEventMapper)
                ) {
                    val ctx = SkiaContext2d(drawContext.canvas.nativeCanvas, SkiaFontManager())

                    plotCanvasFigure2.paint(ctx)
                    //drawSvgContent(svgView, this, canvasSize, redrawTrigger)
                }

            }
        }
    }
}

private fun containsBackground(modifier: Modifier): Boolean {
    return modifier.foldIn(false) { hasBg, element ->
        hasBg || element.toString().contains("BackgroundElement")
    }
}
