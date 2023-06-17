package demo.svgMapping

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import org.jetbrains.letsPlot.skia.android.ui.SvgPanel
import svgModel.DemoModelA
import svgModel.DemoModelB
import svgModel.DemoModelC

class MainActivity_saved : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setContentView(layout, layout.layoutParams)

        // Svg pictures A, B, C
        layout.addView(SvgPanel(this, DemoModelA.createModel()))
        layout.addView(SvgPanel(this, DemoModelB.createModel()))
        layout.addView(SvgPanel(this, DemoModelC.createModel()))
    }
}