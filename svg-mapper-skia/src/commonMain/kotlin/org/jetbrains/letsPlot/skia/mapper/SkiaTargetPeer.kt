/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.mapper

import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.svg.SvgElement
import jetbrains.datalore.vis.svg.SvgTextNode
import jetbrains.datalore.vis.svg.event.SvgEventSpec
import jetbrains.datalore.vis.svg.slim.SvgSlimElements
import jetbrains.datalore.vis.svg.slim.SvgSlimNode
import jetbrains.datalore.vis.svgMapper.TargetPeer
import org.jetbrains.letsPlot.skia.mapper.SvgUtils.getChildren
import org.jetbrains.letsPlot.skia.mapper.SvgUtils.newElement
import org.jetbrains.letsPlot.skia.pane.*

internal class SkiaTargetPeer : TargetPeer<Element> {
    override fun appendChild(target: Element, child: Element) {
        getChildren(target as Group).add(child)
    }

    override fun removeAllChildren(target: Element) {
        if (target is Group) {
            getChildren(target).clear()
        }
    }

    override fun newSvgElement(source: SvgElement): Element {
        return newElement(source)
    }

    override fun newSvgTextNode(source: SvgTextNode): Element {
        TODO() // return Text(source.textContent().get())
    }

    override fun newSvgSlimNode(source: SvgSlimNode): Element {
        return when (source.elementName) {
            SvgSlimElements.GROUP -> Group()
            SvgSlimElements.LINE -> Line()
            SvgSlimElements.CIRCLE -> Circle()
            SvgSlimElements.RECT -> Rectangle()
            SvgSlimElements.PATH -> Path()
            else -> throw IllegalStateException("Unsupported slim node " + source::class.simpleName + " '" + source.elementName + "'")
        }
    }

    override fun setAttribute(target: Element, name: String, value: String) {
        SvgUtils.setAttribute(target, name, value)
    }

    override fun hookEventHandlers(source: SvgElement, target: Element, eventSpecs: Set<SvgEventSpec>): Registration {
        error("UNSUPPORTED: hookEventHandlers")
    }
}