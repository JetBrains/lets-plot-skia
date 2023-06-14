package jetbrains.datalore.vis.svgMapper.skia

import android.content.Context
import android.view.View
import jetbrains.datalore.plot.MonolithicCommon

fun Context.plotView(
    rawPlotSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean = true,
    computationMessagesHandler: ((List<String>) -> Unit) = {}
): View {
    val processedSpec = MonolithicCommon.processRawSpecs(rawPlotSpec, frontendOnly = false)
    return PlotView(this, processedSpec, preserveAspectRatio, computationMessagesHandler)
}

