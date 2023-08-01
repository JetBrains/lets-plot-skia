/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.svg.mapper

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgStyleElement
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet
import org.jetbrains.letsPlot.skia.shape.Group

internal class SvgStyleElementMapper(
    source: SvgStyleElement,
    target: Group,
    peer: SvgSkiaPeer,
) : SvgElementMapper<SvgStyleElement, Group>(source, target, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        val styleSheet = StyleSheet.fromCSS(
            css = source.resource.css(),
            defaultFamily = "Helvetica",
            defaultSize = 15.0
        )
        peer.applyStyleSheet(styleSheet)
    }
}