package me.ikupriyanov.letsPlot.android.demo

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plotDemo.model.plotConfig.Area
import jetbrains.datalore.vis.svgMapper.common.DemoModelA
import jetbrains.datalore.vis.svgMapper.skia.plotView
import jetbrains.datalore.vis.svgMapper.skia.svgView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        setContentView(layout, layout.layoutParams)

        layout.addView(svgView(DemoModelA.createModel()))
        //layout.addView(svgView(DemoModelB.createModel()))
        //layout.addView(addSvg("Demo C", DemoModelC.createModel()))


        layout.addView(
            plotView(
                plotSpec = Area().plotSpecList().first(),
                plotSize = DoubleVector(400.0, 300.0),
                null
            ) {  }
        )
    }
}
