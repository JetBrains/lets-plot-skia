/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose

import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.interact.FigureImplicitInteractionSpecs
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModel

class PlotFigureModel(
    val onUpdateView: (Map<String, Any>?) -> Unit
) : FigureModel {
    private var toolEventCallback: ((Map<String, Any>) -> Unit)? = null

    var toolEventDispatcher: ToolEventDispatcher? = null
        set(value) {
            // De-activate and re-activate ongoing interactions when replacing the dispatcher.
            val wereInteractions = field?.deactivateAllSilently() ?: emptyMap()
            field = value
            value?.let { newDispatcher ->
                newDispatcher.initToolEventCallback { event -> toolEventCallback?.invoke(event) }

                // reactivate interactions in new plot component
                wereInteractions.forEach { (origin, interactionSpecList) ->
                    newDispatcher.activateInteractions(origin, interactionSpecList)
                }
            }
        }

    init {
        toolEventDispatcher?.initToolEventCallback { event -> toolEventCallback?.invoke(event) }
    }

    override fun onToolEvent(callback: (Map<String, Any>) -> Unit) {
        toolEventCallback = callback

        // Make snsure that 'implicit' interaction activated.
        deactivateInteractions(origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT)
        activateInteractions(
            origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT,
            interactionSpecList = FigureImplicitInteractionSpecs.LIST
        )

    }
    override fun activateInteractions(origin: String, interactionSpecList: List<Map<String, Any>>) {
        toolEventDispatcher?.activateInteractions(origin, interactionSpecList)
    }

    override fun deactivateInteractions(origin: String){
        toolEventDispatcher?.deactivateInteractions(origin)
    }

    override fun updateView(specOverride: Map<String, Any>?) {
        onUpdateView(specOverride)
    }
}