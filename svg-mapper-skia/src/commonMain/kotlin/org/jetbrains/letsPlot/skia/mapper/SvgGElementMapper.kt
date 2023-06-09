/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.mapper

import jetbrains.datalore.mapper.core.Synchronizers
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svgMapper.SvgNodeSubtreeGeneratingSynchronizer
import org.jetbrains.letsPlot.skia.pane.Group

internal class SvgGElementMapper(
    source: SvgGElement,
    target: Group,
    peer: SvgSkiaPeer
) : SvgElementMapper<SvgGElement, Group>(source, target, peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        if (!source.isPrebuiltSubtree) {
            val targetList = SvgUtils.elementChildren(target)
            conf.add(
                Synchronizers.forObservableRole(
                    this,
                    source.children(),
                    targetList,
                    SvgNodeMapperFactory(peer)
                )
            )
        } else {
//            UNSUPPORTED("isPrebuiltSubtree")
            conf.add(
                SvgNodeSubtreeGeneratingSynchronizer(
                    source,
                    target,
                    SkiaTargetPeer()
                )
            )
        }
    }
}