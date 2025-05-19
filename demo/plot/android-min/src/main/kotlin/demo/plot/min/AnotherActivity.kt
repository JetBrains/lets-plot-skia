/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.min

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import org.jetbrains.letsPlot.android.canvas.CanvasView
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import plotSpec.DensitySpec

class AnotherActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val densityPlot = DensitySpec().createFigure()

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setContentView(layout, layout.layoutParams)

        layout.addView(Button(this).apply {
            text = "ASD"
            setOnClickListener {
                println("Back button clicked")
            }
        })
        layout.addView(
            CanvasView(this).apply {
                figure = MonolithicCanvas.buildPlotFigureFromRawSpec(densityPlot.toSpec()) {}
                setBackgroundColor(Color.GREEN)
            }
        )
    }
}