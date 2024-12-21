/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.DefaultToolbarController
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModelAdapter
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.ToggleTool
import org.jetbrains.letsPlot.skia.builderLW.ViewModel

@Composable
fun DefaultToolbar() {
    Row {
        Button(onClick = { println("PAN_TOOL_SPEC") }) {
            "PAN"
        }
        Button(onClick = { println("BBOX_ZOOM_TOOL_SPEC") }) {
            "BBOX ZOOM"
        }
        Button(onClick = { println("CBOX_ZOOM_TOOL_SPEC") }) {
            "Rest"
        }
    }
}

class DefaultToolbarAS {

    private val controller = DefaultToolbarController(
        figure = FigureModelAdapterCompose()
    )
    private var figureModel: ViewModel? = null

    inner class FigureModelAdapterCompose : FigureModelAdapter {
        override fun activateTool(@Suppress("NON_EXPORTABLE_TYPE") tool: ToggleTool) {
            if (!tool.active) {
                figureModel?.activateInteractions(
                    origin = tool.name,
                    interactionSpecList = tool.interactionSpecList
                ) ?: println( "The toolbar is unbound." )
            }
        }

        override fun deactivateTool(@Suppress("NON_EXPORTABLE_TYPE") tool: ToggleTool) {
            if (tool.active) {
                figureModel?.deactivateInteractions(tool.name)
                    ?: println("The toolbar is unbound.")
            }
        }

        override fun updateView(specOverride: Map<String, Any>?) {
            figureModel?.updateView(specOverride)
        }

        override fun showError(msg: String) {
            //window.alert(msg)
        }
    }

}