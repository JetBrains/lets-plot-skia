package org.jetbrains.letsPlot.skia.awt.view

import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.DisposableRegistration
import jetbrains.datalore.base.registration.DisposingHub
import jetbrains.datalore.vis.svg.SvgConstants
import jetbrains.datalore.vis.svg.SvgElementListener
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.skia.svg.view.SkikoViewEventDispatcher
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import javax.swing.JPanel

class SvgPanel(
    svg: SvgSvgElement,
    eventDispatcher: SkikoViewEventDispatcher? = null
) : JPanel(), Disposable, DisposingHub {

    /**
     *  Use to create a simple SVG view without event handling.
     */
    constructor(
        svg: SvgSvgElement
    ) : this(
        svg = svg,
        eventDispatcher = null
    )

    private val skikoView = SvgSkikoViewAwt(svg, eventDispatcher)
    private val registrations = CompositeRegistration()

    val eventDispatcher: SkikoViewEventDispatcher
        get() {
            return skikoView.eventDispatcher ?: throw IllegalStateException("No SkikoViewEventDispatcher.")
        }

    init {
        layout = null
        border = null // BorderFactory.createLineBorder(Color.ORANGE, 1)
        skikoView.skiaLayer.bounds = Rectangle(Point(0, 0), skikoView.skiaLayer.preferredSize)
        skikoView.skiaLayer.attachTo(this)

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

    override fun getPreferredSize(): Dimension {
        return skikoView.skiaLayer.preferredSize
    }

    override fun paintComponent(g: Graphics?) {
        // Layout in the parent component (namely GridBagLayout in PlotPanel)
        // sets child size to 1 x 1 px during window re-sizing.
        // Ignore "paint" while thw window is being re-sized.
        if (width > 1 && height > 1) {
            super.paintComponent(g)
        }
    }

    override fun registerDisposable(disposable: Disposable) {
        registrations.add(DisposableRegistration(disposable))
    }

    override fun dispose() {
        registrations.dispose()
        skikoView.dispose()
        removeAll()
    }
}
