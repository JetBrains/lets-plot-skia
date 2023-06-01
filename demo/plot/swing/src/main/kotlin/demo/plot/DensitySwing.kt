package demo.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.svgMapper.skia.plotComponent
import org.jetbrains.letsPlot.intern.toSpec
import plotSpec.DensitySpec
import java.awt.FlowLayout
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

fun main() {
    val rawPlotSpec = DensitySpec().createFigure().toSpec()

    val plotSize = DoubleVector(600.0, 400.0)

    SwingUtilities.invokeLater {

        val plotPanel = plotComponent(
            plotSpec = rawPlotSpec,
            plotSize = plotSize
        ) { computationMessages ->
            computationMessages.forEach {
                println("Plot message: $it")
            }
        }

        val frame = JFrame("Density Plot (Swing)")
        frame.contentPane.layout = FlowLayout()
        frame.contentPane.add(plotPanel)
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.pack()
        frame.setLocationRelativeTo(null)  // move to the screen center
        frame.isVisible = true
    }
}