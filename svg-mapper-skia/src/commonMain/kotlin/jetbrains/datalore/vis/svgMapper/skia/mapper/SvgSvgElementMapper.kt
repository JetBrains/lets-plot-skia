/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package jetbrains.datalore.vis.svgMapper.skia.mapper

import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.mapper.core.Synchronizers
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svgMapper.skia.mapper.drawing.Pane

internal class SvgSvgElementMapper(
    source: SvgSvgElement,
    peer: SvgSkiaPeer
) : SvgElementMapper<SvgSvgElement, Pane>(source, createTargetContainer(), peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        val targetList = SvgUtils.elementChildren(target)
        conf.add(
            Synchronizers.forObservableRole(
                this,
                source.children(),
                targetList,
                SvgNodeMapperFactory(peer)
            )
        )
    }

    override fun onAttach(ctx: MappingContext) {
        super.onAttach(ctx)

        if (!source.isAttached()) {
            throw IllegalStateException("Element must be attached")
        }
//        val peer = SvgAwtPeer()
        source.container().setPeer(peer)
    }

    override fun onDetach() {
        if (source.isAttached()) {
            source.container().setPeer(null)
        }
        super.onDetach()
    }

    companion object {
        private fun createTargetContainer(): Pane {
            val pane = Pane()
//            val pane = StackPane()
//            pane.alignmentProperty().set(Pos.TOP_LEFT)
                    
            // this makes lines sharp
            //group.scaleX = 1 / ScaleFactor.value
            //group.scaleY = 1 / ScaleFactor.value

            //group.centerShapeProperty().set(false)

//            pane.style = "-fx-border-color: red; -fx-border-width: 0 5; -fx-background-color: #2f4f4f"
//            pane.style = "-fx-border-width: 0"
//            pane.snapToPixelProperty().set(true)
//            pane.style = "-fx-padding: 0"

            return pane
        }
    }
}