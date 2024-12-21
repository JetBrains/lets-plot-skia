/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose.desktop

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.skia.awt.view.SvgPanel
import org.jetbrains.letsPlot.skia.builderLW.ViewModel
import java.awt.Cursor
import java.awt.Rectangle
import javax.swing.JPanel

class PlotContainer : JPanel() {
    private val plotSvgPanel = SvgPanel()
    private var viewModel: ViewModel? = null

    init {
        layout = null // plot redraw may jump without this
        cursor = Cursor(Cursor.CROSSHAIR_CURSOR)
        add(plotSvgPanel)
    }

    fun updatePlotView(viewModel: ViewModel, preferredSize: DoubleVector, position: DoubleVector) {
        this.viewModel = viewModel

        plotSvgPanel.svg = viewModel.svg
        plotSvgPanel.eventDispatcher = viewModel.eventDispatcher
        plotSvgPanel.bounds = Rectangle(
            position.x.toInt(),
            position.y.toInt(),
            preferredSize.x.toInt(),
            preferredSize.y.toInt(),
        )
    }

    fun dispose() {
        check(componentCount == 1) { "Unexpected number of children: $componentCount" }
        check(components[0] == plotSvgPanel) { "Unexpected child: should be SvgPanel but was ${components[0]::class.simpleName}" }

        removeAll()
        plotSvgPanel.dispose()
        viewModel?.dispose()
    }
}
