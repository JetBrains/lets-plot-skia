/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelHelper
import org.jetbrains.letsPlot.core.spec.Option.Meta.Kind.GG_TOOLBAR
import org.jetbrains.letsPlot.core.spec.front.SpecOverrideUtil
import org.jetbrains.letsPlot.core.util.MonolithicCommon.processRawSpecs
import org.jetbrains.letsPlot.core.util.PlotSizeUtil
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skia.builderLW.MonolithicSkiaLW
import org.jetbrains.letsPlot.skia.compose.desktop.PlotContainer
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger

private val LOG = NaiveLogger("PlotPanel")

@Suppress("FunctionName")
@Composable
actual fun PlotPanel(
    figure: Figure,
    preserveAspectRatio: Boolean,
    modifier: Modifier,
    computationMessagesHandler: (List<String>) -> Unit
) {
    // Update density on each recomposition to handle monitor DPI changes (e.g. drag between HIDPI/regular monitor)
    val density = LocalDensity.current.density.toDouble()

    // Should be stored because of the spec_id change on each processRawSpecs() call.
    // Pass figure as remember() argument or SwingPanel update will not be triggered.
    val processedPlotSpec by remember(figure) { mutableStateOf(processRawSpecs(figure.toSpec(), frontendOnly = false)) }
    var panelSize by remember { mutableStateOf(DoubleVector.ZERO) }
    var dispatchComputationMessages by remember { mutableStateOf(true) }
    var specOverrideList by remember { mutableStateOf(emptyList<Map<String, Any>>()) }
    val plotContainer by remember { mutableStateOf(PlotContainer()) }
    var figureModel by remember { mutableStateOf<FigureModel?>(null) }

    DisposableEffect(plotContainer) {
        onDispose {
            plotContainer.dispose()
        }
    }

    Column {
        if (figureModel != null && GG_TOOLBAR in processedPlotSpec) {
            PlotToolbar(figureModel!!)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { newSize ->
                    panelSize = DoubleVector(newSize.width / density, newSize.height / density)
                }
                .background(Color.Gray)
        ) {
            SwingPanel(
                background = Color.White,
                modifier = modifier,
                factory = { plotContainer },
                update = { plotViewContainer ->
                    LOG.print("SwingPanel.update()")

                    val plotSpec = SpecOverrideUtil.applySpecOverride(processedPlotSpec, specOverrideList).toMutableMap()

                    // Calculate size here, not outside the SwingPanel.update()
                    // Otherwise on resizing the plot size will lag behind the panel size, causing rendering issues.
                    val plotSize = PlotSizeUtil.preferredFigureSize(plotSpec, preserveAspectRatio, panelSize)
                    val position = DoubleVector(
                        maxOf(0.0, (panelSize.x - plotSize.x) / 2.0),
                        maxOf(0.0, (panelSize.y - plotSize.y) / 2.0)
                    )

                    val viewModel = MonolithicSkiaLW.buildPlotFromProcessedSpecs(plotSpec, plotSize) { messages ->
                        if (dispatchComputationMessages) {
                            // do once
                            dispatchComputationMessages = false
                            computationMessagesHandler(messages)
                        }
                    }

                    if (figureModel == null) {
                        figureModel = FigureModel(
                            onUpdateView = { specOverride ->
                                specOverrideList = FigureModelHelper.updateSpecOverrideList(
                                    specOverrideList = specOverrideList,
                                    newSpecOverride = specOverride
                                )
                            }
                        )
                    }

                    figureModel!!.toolEventDispatcher = viewModel.toolEventDispatcher

                    plotViewContainer.updatePlotView(viewModel, plotSize, position)
                }
            )
        }
    }
}
