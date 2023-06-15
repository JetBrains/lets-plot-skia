package org.jetbrains.letsPlot.skia.compose.desktop

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.plot.MonolithicCommon
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.skia.compose.util.PlotSizeUtil
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.skia.awt.MonolithicSkiaAwt
import java.awt.Component
import java.awt.Cursor
import java.awt.Rectangle
import javax.swing.JPanel
import javax.swing.Timer

internal class PlotComponentProvider(
    figure: Figure,
    private val preserveAspectRatio: Boolean,
    repaintDelay: Int, // ms
    private val computationMessagesHandler: ((List<String>) -> Unit)
) {

    private var dispatchComputationMessages = true

    private val container = JPanel().apply {
        isOpaque = false
//        background = color
        layout = null
        cursor = Cursor(Cursor.CROSSHAIR_CURSOR)
    }

    private var containerSize: DoubleVector? = null

    private val refreshTimer: Timer = Timer(repaintDelay) {
        buildPlotComponent(containerSize)
    }.apply { isRepeats = false }

    private val processedSpec: Map<String, Any>

    init {
        val rawSpec = figure.toSpec()
        processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)
    }

    val factory: () -> Component = { container }

    fun onGloballyPositioned(w: Float, h: Float) {
        disposeInner()

        containerSize = DoubleVector(w.toDouble(), h.toDouble())
        if (refreshTimer.isRunning) {
            refreshTimer.restart()
        } else {
            refreshTimer.start()
        }
    }

    private fun buildPlotComponent(containerSize: DoubleVector?) {
        if (containerSize == null) return

        val plotSize = PlotSizeUtil.preferredFigureSize(
            processedSpec,
            preserveAspectRatio,
            containerSize
        )

        val plotX = if (plotSize.x >= containerSize.x) 0.0 else {
            (containerSize.x - plotSize.x) / 2
        }
        val plotY = if (plotSize.y >= containerSize.y) 0.0 else {
            (containerSize.y - plotSize.y) / 2
        }

        val plotComponent = MonolithicSkiaAwt.buildPlotFromProcessedSpecs(
            plotSize = plotSize,
            plotSpec = processedSpec as MutableMap<String, Any>,
        ) { messages ->
            if (dispatchComputationMessages) {
                // do once
                dispatchComputationMessages = false
                computationMessagesHandler(messages)
            }
        }

        plotComponent.bounds = Rectangle(
            plotX.toInt(),
            plotY.toInt(),
            plotSize.x.toInt(),
            plotSize.y.toInt(),
        )
        container.add(plotComponent)
    }


    fun onDispose() {
        containerSize = null
        if (refreshTimer.isRunning) {
            refreshTimer.stop()
        }
        disposeInner()
    }

    private fun disposeInner() {
        for (component in container.components) {
            if (component is Disposable) {
                component.dispose()
            }
        }
        container.removeAll()
    }
}