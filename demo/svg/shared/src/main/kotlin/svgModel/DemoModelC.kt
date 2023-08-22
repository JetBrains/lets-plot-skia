/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package svgModel

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.datamodel.svg.dom.*

object DemoModelC {
    fun createModel(): SvgSvgElement {
        val clipRect = DoubleRectangle(0.0, 140.0, 200.0, 100.0)
        val svgRoot = SvgSvgElement()
        svgRoot.height().set(400.0)
        svgRoot.width().set(200.0)

        val defs = SvgDefsElement()
        val clip = SvgClipPathElement()
        clip.id().set("myClip")
        defs.children().add(clip)
        val svgClipRect = SvgRectElement(clipRect)
        clip.children().add(svgClipRect)

        val rect = SvgRectElement()
        rect.x().set(10.0)
        rect.y().set(100.0)
        rect.height().set(180.0)
        rect.width().set(180.0)
        rect.clipPath().set(SvgIRI("myClip"))
        rect.fill().set(SvgColors.BLACK)
        rect.setAttribute(SvgGraphicsElement.CLIP_BOUNDS_JFX, clipRect)

        val ellipse = SvgEllipseElement()
        ellipse.cx().set(100.0)
        ellipse.cy().set(190.0)
        ellipse.rx().set(50.0)
        ellipse.ry().set(50.0)
        ellipse.fill().set(SvgColors.RED)

        svgRoot.children().add(defs)
        svgRoot.children().add(rect)
        svgRoot.children().add(ellipse)

        return svgRoot
    }
}