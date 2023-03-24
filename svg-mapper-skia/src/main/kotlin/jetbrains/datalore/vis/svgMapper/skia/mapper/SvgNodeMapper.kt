/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia.mapper


import jetbrains.datalore.mapper.core.Mapper
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.vis.svg.SvgNode
import jetbrains.datalore.vis.svgMapper.skia.mapper.drawing.Element

internal open class SvgNodeMapper<SourceT : SvgNode, TargetT : Element>(
        source: SourceT,
        target: TargetT,
        protected val peer: SvgSkiaPeer
) : Mapper<SourceT, TargetT>(source, target) {

    override fun onAttach(ctx: MappingContext) {
        super.onAttach(ctx)

        peer.registerMapper(source, this)
    }

    override fun onDetach() {
        super.onDetach()

        peer.unregisterMapper(source)
    }
}