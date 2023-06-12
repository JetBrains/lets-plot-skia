package demo.plot

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import jetbrains.datalore.vis.svgMapper.skia.plotView
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.DensitySpec
import plotSpec.PlotGridSpec

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setContentView(layout, layout.layoutParams)

        val rawDensitySpec = DensitySpec().createFigure().toSpec()
        val rawPlotGridSpec = PlotGridSpec().createFigure().toSpec()
        layout.addView(plotView(rawPlotSpec = rawDensitySpec))
        layout.addView(plotView(rawPlotSpec = rawPlotGridSpec))
    }
}