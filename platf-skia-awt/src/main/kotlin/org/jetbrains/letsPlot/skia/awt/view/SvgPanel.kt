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
import org.jetbrains.letsPlot.skia.view.SkikoViewEventDispatcher
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Point
import java.awt.Rectangle
import javax.swing.JPanel

class SvgPanel() : JPanel(), Disposable, DisposingHub {
    var svg: SvgSvgElement = SvgSvgElement()
        set(value) {
            field = value
            skikoView.svg = value
            skikoView.skiaLayer.bounds = Rectangle(Point(0, 0), skikoView.skiaLayer.preferredSize)
        }

    var eventDispatcher: SkikoViewEventDispatcher?
        get() = skikoView.eventDispatcher
        set(value) {
            skikoView.eventDispatcher = value
        }

    /**
     *  Use to create a simple SVG view without event handling.
     */
    constructor(svg: SvgSvgElement) : this() {
        this.svg = svg
        this.eventDispatcher = null
    }

    constructor(svg: SvgSvgElement, eventDispatcher: SkikoViewEventDispatcher? = null) : this() {
        this.svg = svg
        this.eventDispatcher = eventDispatcher
    }

    private val skikoView = SvgSkikoViewAwt()
    private val registrations = CompositeRegistration()

    init {
        layout = null
        border = null // BorderFactory.createLineBorder(Color.ORANGE, 1)
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
        // Order matters or:
        // Exception in thread "AWT-EventQueue-0" java.lang.IllegalStateException: SkiaLayer is disposed
        //   at org.jetbrains.skiko.SkiaLayer.needRedraw(SkiaLayer.awt.kt:518)

        // 1. Dispose SvgSkikoView first. So it won't ask SkiaLayerAwt to redraw.
        skikoView.dispose()

        // 2. If dispose is not in order this line causes changes in SVG, SvgSkikoView handles them
        // and ask disposed SkiaLayerAwt to redraw.
        // Not sure why SkiaLayerAwt is disposed at this point - may be post events from SVG, received after removeAll()?
        registrations.dispose()

        // 3. Now it's safe to remove SkiaLayerAwt from the parent component.
        removeAll()
    }
}
