package demo.plot

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import jetbrains.datalore.vis.svgMapper.skia.plotView
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.DensitySpec

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setContentView(layout, layout.layoutParams)

        val rawPlotSpec = DensitySpec().createFigure().toSpec()
        layout.addView(plotView(rawPlotSpec = rawPlotSpec))
    }
}