/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skiko.swing

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.skia.MonolithicSkikoSwing
import jetbrains.datalore.vis.swing.PlotSpecComponentProvider
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants

internal class PlotComponentProviderSkiko(
    private val processedSpec: MutableMap<String, Any>,
    private val preserveAspectRatio: Boolean,
    private val computationMessagesHandler: (List<String>) -> Unit
) : PlotSpecComponentProvider(
    processedSpec = processedSpec,
    preserveAspectRatio = preserveAspectRatio,
    svgComponentFactory = DUMMY_SVG_COMPONENT_FACTORY,
    executor = DUMMY_EXECUTOR,
    computationMessagesHandler = computationMessagesHandler
) {

    // ToDo: include the "monolithic" staff in "svgComponentFactory"
    // So that we don't need to override the entire 'createComponent' here.
    override fun createComponent(containerSize: Dimension?): JComponent {
        val plotSize = containerSize?.let {
            val preferredSize = getPreferredSize(containerSize)
            DoubleVector(preferredSize.width.toDouble(), preferredSize.height.toDouble())
        }

        return MonolithicSkikoSwing.buildPlotFromProcessedSpecs(
            plotSize = plotSize,
            plotSpec = processedSpec,
            plotMaxWidth = null,
            computationMessagesHandler = computationMessagesHandler
        )
    }

    /**
     * Override when used in IDEA plugin.
     * Use: JBScrollPane
     */
    override fun createScrollPane(plotComponent: JComponent): JScrollPane {
        return JScrollPane(
            plotComponent,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        ).apply {
            border = null
        }
    }

    companion object {
//        private val LOG = PortableLogging.logger(DefaultPlotComponentProvider::class)

        //        private val SVG_COMPONENT_FACTORY_SKIA_SWING = { svg: SvgSvgElement ->
//            SvgPanelDesktop(svg)
//        }
        private val DUMMY_SVG_COMPONENT_FACTORY = { svg: SvgSvgElement ->
            UNSUPPORTED("This component factory should not be invoked.")
        }
        private val DUMMY_EXECUTOR: (() -> Unit) -> Unit = {
            UNSUPPORTED("This 'executor' should not be invoked.")
        }
    }
}