/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelHelper
import org.jetbrains.letsPlot.core.spec.Option.Meta.Kind.GG_TOOLBAR
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.front.SpecOverrideUtil
import org.jetbrains.letsPlot.core.util.MonolithicCommon.processRawSpecs
import org.jetbrains.letsPlot.core.util.PlotThemeHelper
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.skia.builderLW.MonolithicSkiaLW
import org.jetbrains.letsPlot.skia.compose.desktop.PlotContainer
import org.jetbrains.letsPlot.skia.compose.desktop.SvgViewPanel
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger

private val LOG = NaiveLogger("PlotPanel")

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

    // Background

    // Check if the modifier already has a background
    val hasBackground = modifier.foldIn(false) { hasBg, element ->
        hasBg || element.toString().contains("BackgroundElement")
    }

    // Apply a plot background only if the user hasn't set one
    val finalModifier = if (!hasBackground) {
        val lpBackground = rawSpec.let {
            val lpColor = PlotThemeHelper.plotBackground(rawSpec)
            Color(lpColor.red, lpColor.green, lpColor.blue, lpColor.alpha)
        }
        modifier.background(lpBackground)
    } else {
        modifier
    }

    // Update density on each recomposition to handle monitor DPI changes (e.g., drag between HIDPI/regular monitor)
    val density = LocalDensity.current.density.toDouble()

    // Cache processed plot spec to avoid reprocessing the same raw spec on every recomposition.
    val processedPlotSpec by remember(rawSpec) {
        mutableStateOf(processRawSpecs(rawSpec, frontendOnly = false))
    }

    var panelSize by remember { mutableStateOf(DoubleVector.ZERO) }
    var dispatchComputationMessages by remember { mutableStateOf(true) }
    var specOverrideList by remember { mutableStateOf(emptyList<Map<String, Any>>()) }
    val plotContainer by remember { mutableStateOf(PlotContainer()) }
    var plotFigureModel by remember { mutableStateOf<PlotFigureModel?>(null) }

    DisposableEffect(plotContainer) {
        onDispose {
            plotContainer.dispose()
        }
    }

    Column(modifier = finalModifier) {
        if (plotFigureModel != null && GG_TOOLBAR in processedPlotSpec) {
            PlotToolbar(plotFigureModel!!)
        }

        Box(
            modifier = finalModifier
                .weight(1f) // Take the remaining vertical space
                .fillMaxWidth() // Fill available width
                .onSizeChanged { newSize ->
                    // Convert logical pixels (from Compose layout) to physical pixels (plot SVG pixels)
                    panelSize = DoubleVector(newSize.width / density, newSize.height / density)
                }
        ) {
            if (PlotConfig.isFailure(processedPlotSpec)) {
                BasicTextField(
                    value = PlotConfig.getErrorMessage(processedPlotSpec),
                    onValueChange = { },
                    readOnly = true,
                    textStyle = errorTextStyle,
                    modifier = errorModifier
                )
            } else {
                LaunchedEffect(panelSize, processedPlotSpec, specOverrideList) {
                    if (panelSize != DoubleVector.ZERO) {
                        LOG.print("Plot update triggered")

                        val plotSpec =
                            SpecOverrideUtil.applySpecOverride(processedPlotSpec, specOverrideList).toMutableMap()

                        val viewModel = MonolithicSkiaLW.buildPlotFromProcessedSpecs(
                            plotSpec = plotSpec,
                            containerSize = panelSize,
                            sizingPolicy = SizingPolicy.fitContainerSize(preserveAspectRatio)
                        ) { messages ->
                            if (dispatchComputationMessages) {
                                // do once
                                dispatchComputationMessages = false
                                computationMessagesHandler(messages)
                            }
                        }

                        if (plotFigureModel == null) {
                            plotFigureModel = PlotFigureModel(
                                onUpdateView = { specOverride ->
                                    specOverrideList = FigureModelHelper.updateSpecOverrideList(
                                        specOverrideList = specOverrideList,
                                        newSpecOverride = specOverride
                                    )
                                }
                            )
                        }

                        plotFigureModel!!.toolEventDispatcher = viewModel.toolEventDispatcher

                        val plotWidth = viewModel.svg.width().get() ?: panelSize.x
                        val plotHeight = viewModel.svg.height().get() ?: panelSize.y

                        // Calculate centering position in physical pixels
                        // Both panelSize and plot dimensions are in physical pixels
                        val position = DoubleVector(
                            maxOf(0.0, (panelSize.x - plotWidth) / 2.0),
                            maxOf(0.0, (panelSize.y - plotHeight) / 2.0)
                        )

                        plotContainer.updateViewModel(viewModel, position, density.toFloat())
                    }
                }

                SvgViewPanel(
                    svgView = plotContainer.svgView,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
