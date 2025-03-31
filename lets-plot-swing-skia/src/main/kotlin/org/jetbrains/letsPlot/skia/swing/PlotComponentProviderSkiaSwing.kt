/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.swing

import org.jetbrains.letsPlot.awt.plot.component.PlotComponentProvider
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.skia.awt.builderHW.MonolithicSkiaAwt
import java.awt.Dimension
import javax.swing.JComponent

internal class PlotComponentProviderSkiaSwing(
    private val processedSpec: MutableMap<String, Any>,
    private val preserveAspectRatio: Boolean,
    private val computationMessagesHandler: (List<String>) -> Unit
) : PlotComponentProvider {

    override fun createComponent(containerSize: Dimension?, sizingPolicy: SizingPolicy, specOverrideList: List<Map<String, Any>>): JComponent {
        val plotSize = containerSize?.let {
            SizingPolicy.fitContainerSize(preserveAspectRatio)
            val preferredSize = containerSize
            DoubleVector(preferredSize.width.toDouble(), preferredSize.height.toDouble())
        }

        return MonolithicSkiaAwt.buildPlotFromProcessedSpecs(
            plotSpec = processedSpec,
            containerSize = containerSize?.let { DoubleVector(it.width.toDouble(), it.height.toDouble()) },
            sizingPolicy = sizingPolicy,
            computationMessagesHandler = computationMessagesHandler
        )
    }

    companion object {
        private val DUMMY_SVG_COMPONENT_FACTORY = { _: SvgSvgElement ->
            UNSUPPORTED("This component factory should not be invoked.")
        }
        private val DUMMY_EXECUTOR: (() -> Unit) -> Unit = {
            UNSUPPORTED("This 'executor' should not be invoked.")
        }
    }
}