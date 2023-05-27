package demo.svgMapping

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import svgModel.DemoModelA
import svgModel.DemoModelB
import svgModel.DemoModelC
import jetbrains.datalore.vis.svgMapper.skia.svgView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setContentView(layout, layout.layoutParams)

        // Svg pictures A, B, C
        layout.addView(svgView(DemoModelA.createModel()))
        layout.addView(svgView(DemoModelB.createModel()))
        layout.addView(svgView(DemoModelC.createModel()))
    }
}