package me.ikupriyanov.letsPlot.android.demo

import android.app.Activity
import android.os.Bundle
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import jetbrains.datalore.plotDemo.model.plotConfig.Area
import jetbrains.datalore.plotDemo.model.plotConfig.PlotGrid
import jetbrains.datalore.vis.svgMapper.common.DemoModelB
import jetbrains.datalore.vis.svgMapper.skia.plotView
import jetbrains.datalore.vis.svgMapper.skia.svgView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        setContentView(layout, layout.layoutParams)

        //basicDemo(layout)
        plotGridDemo(layout)
    }

    private val Number.dp get() = TypedValue.applyDimension(COMPLEX_UNIT_DIP, toFloat(), resources.displayMetrics)

    private fun basicDemo(layout: LinearLayout) {
        layout.addView(plotView(rawPlotSpec = Area().plotSpecList()[1]), 360.dp.toInt(), 120.dp.toInt())
        layout.addView(plotView(rawPlotSpec = Area().plotSpecList()[0]))
        layout.addView(svgView(DemoModelB.createModel()))
    }

    private fun plotGridDemo(layout: LinearLayout) {
        layout.addView(plotView(rawPlotSpec = PlotGrid().plotSpecList().first()))
    }
}
