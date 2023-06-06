package org.jetbrains.letsPlot.skiko.desktop

import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.DisposableRegistration
import jetbrains.datalore.base.registration.DisposingHub
import jetbrains.datalore.vis.svg.SvgConstants
import jetbrains.datalore.vis.svg.SvgElementListener
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.skiko.SkikoViewEventDispatcher
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.SwingUtilities

class SvgPanelDesktop(
    svg: SvgSvgElement,
    eventDispatcher: SkikoViewEventDispatcher? = null
) : JPanel(), Disposable, DisposingHub {

    private val skikoView = SvgSkikoViewDesktop(svg, eventDispatcher)
    private var registrations = CompositeRegistration()

    init {
        layout = GridLayout(0, 1, 0, 0)
        border = null // BorderFactory.createLineBorder(Color.ORANGE, 1)
        skikoView.skiaLayer.attachTo(this)
        SwingUtilities.invokeLater {
            skikoView.skiaLayer.needRedraw()

            registrations.add(
                svg.addListener(object : SvgElementListener {
                    override fun onAttrSet(event: SvgAttributeEvent<*>) {
                        if (SvgConstants.HEIGHT.equals(event.attrSpec.name, ignoreCase = true) ||
                            SvgConstants.WIDTH.equals(event.attrSpec.name, ignoreCase = true)
                        ) {
                            throw IllegalStateException("Can't change SVG attribute $(event.attrSpec.name)")
                        }
                    }
                })
            )
        }
    }

    override fun getPreferredSize(): Dimension {
        return skikoView.skiaLayer.preferredSize
    }

    override fun registerDisposable(disposable: Disposable) {
        registrations.add(DisposableRegistration(disposable))
    }

    override fun dispose() {
        registrations.dispose()
        skikoView.dispose()
    }
}
