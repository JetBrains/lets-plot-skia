package jetbrains.datalore.vis.svgMapper.skia

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.vis.svg.SvgSvgElement
import org.jetbrains.skiko.SkiaLayer
import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingUtilities

fun svgComponent(svg: SvgSvgElement): JComponent {
    return SkiaWidgetPanel(swingSkiaWidget(svg))
}

fun plotComponent(
    processedSpec: MutableMap<String, Any>,
    plotSize: DoubleVector? = null,
    plotMaxWidth: Double? = null,
): JComponent {
    val messages = mutableListOf<String>()
    val plots = MonolithicSkia.buildPlotFromProcessedSpecs(
        processedSpec,
        plotSize,
        plotMaxWidth,
        messages::addAll
    ).getOrThrow()

    if (plots.size == 1) {
        val plotBuildInfo = plots.single()
        return swingSkiaPlotPanel(plotBuildInfo)
    } else {
        val container = JPanel(null)
        plots.forEach { plotBuildInfo ->
            val plotComponent = swingSkiaPlotPanel(plotBuildInfo)

            val bounds = plotBuildInfo.bounds()
            plotComponent.bounds = Rectangle(
                bounds.origin.x.toInt(),
                bounds.origin.y.toInt(),
                bounds.dimension.x.toInt(),
                bounds.dimension.y.toInt()
            )
            container.add(plotComponent)
        }

        val bunchBounds = plots.map { it.bounds() }
            .fold(DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)) { acc, bounds ->
                acc.union(bounds)
            }

        val bunchDimensions = Dimension(
            bunchBounds.width.toInt(),
            bunchBounds.height.toInt()
        )

        container.preferredSize = bunchDimensions
        container.minimumSize = bunchDimensions
        container.maximumSize = bunchDimensions
        return container
    }
}

private fun swingSkiaPlotPanel(plotBuildInfo: MonolithicCommon.PlotBuildInfo): JPanel {
    val plotAssembler = plotBuildInfo.plotAssembler
    val plot = plotAssembler.createPlot()
    val plotContainer = PlotContainer(plot, plotBuildInfo.size)

    plotContainer.ensureContentBuilt()
    val svg = plotContainer.svg
    val skiaWidget = swingSkiaWidget(svg)
    skiaWidget.setMouseEventListener { s, e ->
        plotContainer.mouseEventPeer.dispatch(s, e)
        SwingUtilities.invokeLater { skiaWidget.nativeLayer.needRedraw() }
    }
    return SkiaWidgetPanel(skiaWidget)
}

private fun swingSkiaWidget(svg: SvgSvgElement): SkiaWidget {
    return SkiaWidget(svg, SkiaLayer()) { skiaLayer, skikoView ->
        // https://github.com/JetBrains/skiko/issues/614
        //skiaLayer.skikoView = skikoView
        skiaLayer.addView(skikoView)
    }
}
