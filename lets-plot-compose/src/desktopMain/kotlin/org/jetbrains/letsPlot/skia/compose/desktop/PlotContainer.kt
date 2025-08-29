/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose.desktop

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.skia.builderLW.ViewModel

class PlotContainer : Disposable {
    internal val svgView = SvgView()
    private var viewModel: ViewModel? = null

    fun updateViewModel(viewModel: ViewModel, position: DoubleVector, pixelDensity: Float) {
        this.viewModel = viewModel

        svgView.svg = viewModel.svg
        svgView.eventDispatcher = viewModel.eventDispatcher
        svgView.setPosition(position.x.toFloat(), position.y.toFloat())
        svgView.setPixelDensity(pixelDensity)
    }

    override fun dispose() {
        svgView.dispose()
        viewModel?.dispose()
    }
}
