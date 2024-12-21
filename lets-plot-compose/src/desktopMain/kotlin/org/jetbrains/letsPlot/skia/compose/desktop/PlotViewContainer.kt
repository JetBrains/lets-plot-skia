/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose.desktop

import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_ORIGIN
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_INTERACTION_TARGET
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_NAME
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.EVENT_RESULT_DATA_BOUNDS
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_ACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_DEACTIVATED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.INTERACTION_UNSUPPORTED
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.ROLLBACK_ALL_CHANGES
import org.jetbrains.letsPlot.core.interact.event.ToolEventSpec.SELECTION_CHANGED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelHelper
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.COORD_XLIM_TRANSFORMED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.COORD_YLIM_TRANSFORMED
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelOptions.TARGET_ID
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleTool
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToolSpecs.BBOX_ZOOM_TOOL_SPEC
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToolSpecs.PAN_TOOL_SPEC
import org.jetbrains.letsPlot.core.spec.front.SpecOverrideUtil
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.PlotSizeUtil
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skia.awt.view.SvgPanel
import org.jetbrains.letsPlot.skia.builderLW.MonolithicSkiaLW
import org.jetbrains.letsPlot.skia.builderLW.ViewModel
import org.jetbrains.letsPlot.skia.compose.util.NaiveLogger
import java.awt.Cursor
import java.awt.Rectangle
import javax.swing.JPanel

private val LOG = NaiveLogger("PlotViewContainer")

class PlotViewContainer(
    private val computationMessagesHandler: ((List<String>) -> Unit)
) : JPanel() {

    private lateinit var plotSvgPanel: SvgPanel
    private var viewModel: ViewModel? = null

    private var needUpdate = false
    private var dispatchComputationMessages = true
    private var processedSpec: Map<String, Any>? = null
    private val panTool = ToggleTool(PAN_TOOL_SPEC)
    private val bboxZoomTool = ToggleTool(BBOX_ZOOM_TOOL_SPEC)
    private val cboxZoomTool = ToggleTool(BBOX_ZOOM_TOOL_SPEC)

    private fun activateTool(tool: ToggleTool) {
        if (!tool.active) {
            viewModel?.activateInteractions(
                origin = tool.name,
                interactionSpecList = tool.interactionSpecList
            ) ?: println("The toolbar is unbound.")
        }
    }

    private fun deactivateTool(tool: ToggleTool) {
        if (tool.active) {
            viewModel?.deactivateInteractions(tool.name)
                ?: println("The toolbar is unbound.")
        }
    }

    private fun updateOverrideSpec(specOverride: Map<String, Any>?) {
        this@PlotViewContainer.specOverride = FigureModelHelper.updateSpecOverrideList(
            specOverrideList = this@PlotViewContainer.specOverride,
            newSpecOverride = specOverride
        )
    }

    private fun handleToolFeedback(event: Map<String, Any>) {
        when (event[EVENT_NAME]) {
            INTERACTION_ACTIVATED, INTERACTION_DEACTIVATED -> {
                val toolName = event[EVENT_INTERACTION_ORIGIN] as String
                val activated = event[EVENT_NAME] == INTERACTION_ACTIVATED
                //tools.find { it.tool.name == toolName }?.let {
                //    it.tool.active = activated
                //    it.view.setState(activated)
                //}
            }

            SELECTION_CHANGED -> {
                event[EVENT_RESULT_DATA_BOUNDS]?.let { bounds ->
                    @Suppress("UNCHECKED_CAST")
                    bounds as List<Double?>
                    val specOverride = HashMap<String, Any>().also { map ->
                        map[COORD_XLIM_TRANSFORMED] = listOf(bounds[0], bounds[2])
                        map[COORD_YLIM_TRANSFORMED] = listOf(bounds[1], bounds[3])
                        event[EVENT_INTERACTION_TARGET]?.let { targetId ->
                            map[TARGET_ID] = targetId
                        }
                    }
                    updateOverrideSpec(specOverride)
                }
            }

            ROLLBACK_ALL_CHANGES -> {
                val targetId = event[EVENT_INTERACTION_TARGET]
                val specOverride = targetId?.let {
                    mapOf(TARGET_ID to targetId)
                }
                updateOverrideSpec(specOverride)
            }

            INTERACTION_UNSUPPORTED -> {
                //viewModel?.showError(
                //    (event[EVENT_RESULT_ERROR_MSG] as? String) ?: "Unspecified error."
                //)
            }

            else -> {}
        }
    }

    var figure: Figure? = null
        set(fig) {
            check(fig != null) { "The 'figure' can't be null." }

            if (field == fig) {
                return
            }

            field = fig
            needUpdate = true

            val rawSpec = fig.toSpec()
            processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
        }

    var preserveAspectRatio: Boolean? = null
        set(v) {
            check(v != null) { "'preserveAspectRatio' value can't be null." }

            if (field == v) {
                return
            }

            // TODO: investigate and report the bug, most likely in Skiko.
            // Switching the aspect ratio causes flickering - extended plot area is rendered with black background.
            // [UPD]: looks like with skiko 0.8.18 this is not needed anymore
            // rebuildSvgPanel()
            field = v
            needUpdate = true
        }

    var panToolEnabled: Boolean = false
        set(v) {
            if (field == v) {
                return
            }

            field = v
            if (v) {
                activateTool(panTool)
            } else {
                deactivateTool(panTool)
            }
        }

    var bboxZoomToolEnabled: Boolean = false
        set(v) {
            if (field == v) {
                return
            }

            field = v
            if (v) {
                activateTool(bboxZoomTool)
            } else {
                deactivateTool(bboxZoomTool)
            }
        }

    var cboxZoomToolEnabled: Boolean = false
        set(v) {
            if (field == v) {
                return
            }

            field = v
            if (v) {
                activateTool(cboxZoomTool)
            } else {
                deactivateTool(cboxZoomTool)
            }
        }

    var size: DoubleVector = DoubleVector.ZERO
        set(v) {
            if (field == v) {
                return
            }

            // TODO: investigate and report the bug, most likely in Skiko.
            // With fixed aspect ratio the horizontal resize causes the plot to be rendered at wrong position, even outside the panel.
            // [UPD]: looks like with skiko 0.8.18 this is not needed anymore
            //rebuildSvgPanel()
            field = v
            needUpdate = true
        }

    private var specOverride: List<Map<String, Any>> = emptyList()
        set(v) {
            if (field == v) {
                return
            }

            field = v
            needUpdate = true
            updatePlotView()
        }

    init {
        LOG.print("New PlotViewContainer preserveAspectRatio: $preserveAspectRatio")
        isOpaque = false
        layout = null
        cursor = Cursor(Cursor.CROSSHAIR_CURSOR)
        rebuildSvgPanel()
    }

    fun updatePlotView() {
        LOG.print("updatePlotView() - needUpdate: $needUpdate, preserveAspectRatio: $preserveAspectRatio size: $size")
        val processedSpec = processedSpec ?: return

        if (!needUpdate) {
            return
        }

        needUpdate = false

        val plotSize = PlotSizeUtil.preferredFigureSize(
            processedSpec,
            preserveAspectRatio ?: throw IllegalStateException("'preserveAspectRatio' not set."),
            size
        )

        val plotX = if (plotSize.x >= size.x) 0.0 else {
            (size.x - plotSize.x) / 2
        }
        val plotY = if (plotSize.y >= size.y) 0.0 else {
            (size.y - plotSize.y) / 2
        }

        plotSvgPanel.bounds = Rectangle(
            plotX.toInt(),
            plotY.toInt(),
            plotSize.x.toInt(),
            plotSize.y.toInt(),
        )

        viewModel?.dispose()

        val plotSpec = SpecOverrideUtil.applySpecOverride(processedSpec, specOverride)
            .toMutableMap() // ToDo: get rid of "mutable"

        viewModel = MonolithicSkiaLW.buildPlotFromProcessedSpecs(
            plotSpec = plotSpec,
            plotSize = plotSize,
        ) { messages ->
            if (dispatchComputationMessages) {
                // do once
                dispatchComputationMessages = false
                computationMessagesHandler(messages)
            }
        }
        viewModel!!.toolEventDispatcher.initToolEventCallback { event -> handleToolFeedback(event) }
        plotSvgPanel.svg = viewModel!!.svg
        plotSvgPanel.eventDispatcher = viewModel!!.eventDispatcher
    }

    fun disposePlotView() {
        check(componentCount == 1) { "Unexpected number of children: $componentCount" }
        check(components[0] == plotSvgPanel) { "Unexpected child: should be SvgPanel but was ${components[0]::class.simpleName}" }

        removeAll()
        plotSvgPanel.dispose()
        viewModel?.dispose()
    }

    private fun rebuildSvgPanel() {
        if (componentCount == 1) {
            removeAll()
            plotSvgPanel.dispose()
            viewModel?.dispose()
        }

        plotSvgPanel = SvgPanel()
        viewModel = null
        add(plotSvgPanel)
    }
}
