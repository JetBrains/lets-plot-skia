package demo.svgMapping

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import org.jetbrains.letsPlot.skiko.android.SvgPanelAndroid
import svgModel.DemoModelA
import svgModel.DemoModelB
import svgModel.DemoModelC

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setContentView(layout, layout.layoutParams)

        // Svg pictures A, B, C
        layout.addView(SvgPanelAndroid(this, DemoModelA.createModel()))
        layout.addView(SvgPanelAndroid(this, DemoModelB.createModel()))
        layout.addView(SvgPanelAndroid(this, DemoModelC.createModel()))
    }
}