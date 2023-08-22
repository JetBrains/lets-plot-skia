/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.awt.view

import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.DisposableRegistration
import org.jetbrains.letsPlot.commons.registration.DisposingHub
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElementListener
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
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
