/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.view

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import org.jetbrains.letsPlot.android.canvas.CanvasView
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import plotSpec.DensitySpec
import java.util.*

class ResizingWithFixedPlotSizePolicyDemoActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val plotFigure = DensitySpec().createFigure()

        val view = CanvasView(this).apply {
            figure = MonolithicCanvas.buildPlotFigureFromRawSpec(
                rawSpec = plotFigure.toSpec(),
                sizingPolicy = SizingPolicy.keepFigureDefaultSize(),
                computationMessagesHandler = {}
            )
            setBackgroundColor(Color.GREEN)
        }

        setupResizableCanvas(view)
    }

    companion object {
        fun Activity.setupResizableCanvas(view: CanvasView) {
            setContentView(view)
            val resizeTask = object : TimerTask() {
                var width = 500
                var height = 500
                var dv = 2

                override fun run() {
                    runOnUiThread {
                        if (width < 500 || width > 1000) {
                            dv *= -1
                        }

                        width += dv
                        height += dv

                        view.layoutParams = FrameLayout.LayoutParams(width, height)
                    }
                }
            }
            Timer().schedule(resizeTask, 0, 10)
        }

    }
}