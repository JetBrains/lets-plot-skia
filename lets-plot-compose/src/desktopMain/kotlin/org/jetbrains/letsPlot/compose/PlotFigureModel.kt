/*
 * Copyright (c) 2024 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.compose

import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.InteractionSpec
import org.jetbrains.letsPlot.core.interact.event.ToolEventDispatcher
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.FigureModel

class PlotFigureModel(
    val onUpdateView: (Map<String, Any>?) -> Unit
) : FigureModel {
    private val toolEventCallbacks = mutableListOf<(Map<String, Any>) -> Unit>()

    var toolEventDispatcher: ToolEventDispatcher? = null
        set(value) {
            // De-activate and re-activate ongoing interactions when replacing the dispatcher.
            val wereInteractions = field?.deactivateAllSilently() ?: emptyMap()
            field = value
            value?.let { newDispatcher ->
                newDispatcher.initToolEventCallback { event ->
                    toolEventCallbacks.forEach { it(event) }
                }

                // reactivate interactions in new plot component
                wereInteractions.forEach { (origin, interactionSpecList) ->
                    newDispatcher.activateInteractions(origin, interactionSpecList)
                }
            }
        }

    init {
        toolEventDispatcher?.initToolEventCallback { event -> toolEventCallbacks.forEach { it.invoke(event) } }
    }

    override fun addToolEventCallback(callback: (Map<String, Any>) -> Unit): Registration {
        toolEventCallbacks.add(callback)

        // Make snsure that 'implicit' interaction activated.
        deactivateInteractions(origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT)
        activateInteractions(
            origin = ToolEventDispatcher.ORIGIN_FIGURE_IMPLICIT,
            interactionSpecList = FIGURE_IMPLICIT_INTERACTIONS
        )

        return object : Registration() {
            override fun doRemove() {
                toolEventCallbacks.remove(callback)
            }
        }
    }
    override fun activateInteractions(origin: String, interactionSpecList: List<InteractionSpec>) {
        toolEventDispatcher?.activateInteractions(origin, interactionSpecList)
    }

    override fun addDisposible(disposable: Disposable) {
        TODO("Not yet implemented")
    }

    override fun deactivateInteractions(origin: String){
        toolEventDispatcher?.deactivateInteractions(origin)
    }

    override fun dispose() {
        toolEventDispatcher?.deactivateAll()
        toolEventDispatcher = null
        toolEventCallbacks.clear()

        //val disposibles = ArrayList(disposibleTools)
        //disposibleTools.clear()
        //disposibles.forEach { it.dispose() }
    }

    override fun setDefaultInteractions(interactionSpecList: List<InteractionSpec>) {
        TODO("Not yet implemented")
    }

    override fun updateView(specOverride: Map<String, Any>?) {
        onUpdateView(specOverride)
    }

    companion object {
        private val FIGURE_IMPLICIT_INTERACTIONS = listOf(InteractionSpec(InteractionSpec.Name.ROLLBACK_ALL_CHANGES))
    }
}