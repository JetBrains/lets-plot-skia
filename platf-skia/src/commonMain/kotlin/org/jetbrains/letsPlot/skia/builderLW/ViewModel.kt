/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.builderLW

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.skia.view.SkikoViewEventDispatcher

sealed class ViewModel(
    val svg: SvgSvgElement,
    val eventDispatcher: SkikoViewEventDispatcher
) : Disposable {
    internal abstract val bounds: Rectangle

    internal open fun collect(dest: SvgSvgElement) {
        dest.children().add(svg)
    }
}

internal class SimpleModel(
    svg: SvgSvgElement
) : ViewModel(svg,
    eventDispatcher = object : SkikoViewEventDispatcher {
        override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {} // ignore events
    }
) {
    override val bounds: Rectangle
        get() = throw IllegalStateException("Not supported: SimpleModel.bounds")

    override fun dispose() {}
}

internal class SinglePlotModel(
    svg: SvgSvgElement,
    eventDispatcher: SkikoViewEventDispatcher,
    override val bounds: Rectangle,
    private val registration: Registration
) : ViewModel(svg, eventDispatcher) {

    override fun dispose() {
        registration.dispose()
    }
}

internal class CompositeFigureModel(
    svg: SvgSvgElement,
    override val bounds: Rectangle,
) : ViewModel(svg, CompositeFigureEventDispatcher()) {
    private val children = ArrayList<ViewModel>()

    fun addChildFigure(childModel: ViewModel) {
        children.add(childModel)
        (eventDispatcher as CompositeFigureEventDispatcher).addEventDispatcher(
            bounds = childModel.bounds,
            eventDispatcher = childModel.eventDispatcher
        )
    }

    fun assembleAsRoot() {
        children.forEach { it.collect(dest = this.svg) }
    }

    override fun collect(dest: SvgSvgElement) {
        dest.children().add(this.svg)
        children.forEach { it.collect(dest) }
    }

    override fun dispose() {
        children.forEach { it.dispose() }
        children.clear()
    }
}