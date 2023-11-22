/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.swing

import org.jetbrains.letsPlot.awt.plot.component.PlotComponentProvider
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.core.util.PlotSizeUtil
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.skia.awt.hw.MonolithicSkiaAwt
import java.awt.Dimension
import javax.swing.JComponent

internal class PlotComponentProviderSkiaSwing(
    private val processedSpec: MutableMap<String, Any>,
    private val preserveAspectRatio: Boolean,
    private val computationMessagesHandler: (List<String>) -> Unit
) : PlotComponentProvider {

    override fun getPreferredSize(containerSize: Dimension): Dimension {
        return getPreferredSize(processedSpec, preserveAspectRatio, containerSize)
    }

    override fun createComponent(containerSize: Dimension?): JComponent {
        val plotSize = containerSize?.let {
            val preferredSize = getPreferredSize(containerSize)
            DoubleVector(preferredSize.width.toDouble(), preferredSize.height.toDouble())
        }

        return MonolithicSkiaAwt.buildPlotFromProcessedSpecs(
            plotSize = plotSize,
            plotSpec = processedSpec,
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

        private fun getPreferredSize(
            processedSpec: MutableMap<String, Any>,
            preserveAspectRatio: Boolean,
            containerSize: Dimension,
        ): Dimension {
//            // ToDo: this isn't looking nice.
//            val sizeEstimator = object : PlotSpecComponentProvider(
//                processedSpec = processedSpec,
//                preserveAspectRatio = preserveAspectRatio,
//                svgComponentFactory = DUMMY_SVG_COMPONENT_FACTORY,
//                executor = DUMMY_EXECUTOR,
//                computationMessagesHandler = { /*no messages when measuring plot size*/ }
//            ) {
//                override fun createScrollPane(plotComponent: JComponent): JScrollPane {
//                    UNSUPPORTED("'createScrollPane()' should not be invoked.")
//                }
//            }
//
//            return sizeEstimator.getPreferredSize(containerSize)
            return PlotSizeUtil.preferredFigureSize(
                figureSpec = processedSpec,
                preserveAspectRatio = preserveAspectRatio,
                containerSize = containerSize.let { DoubleVector(it.width.toDouble(), it.height.toDouble()) }
            ).let {
                Dimension(it.x.toInt(), it.y.toInt())
            }
        }
    }
}