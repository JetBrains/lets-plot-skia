/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.skia.mapper

import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.mapper.core.Synchronizer
import jetbrains.datalore.mapper.core.SynchronizerContext
import jetbrains.datalore.vis.svg.SvgElement
import jetbrains.datalore.vis.svg.SvgElementListener
import jetbrains.datalore.vis.svg.event.SvgAttributeEvent
import jetbrains.datalore.vis.svg.event.SvgEventSpec
import jetbrains.datalore.vis.svgMapper.skia.mapper.drawing.Element

internal open class SvgElementMapper<SourceT : SvgElement, TargetT : Element>(
    source: SourceT,
    target: TargetT,
    peer: SvgSkiaPeer
) : SvgNodeMapper<SourceT, TargetT>(source, target, peer) {

    private var myHandlerRegs: MutableMap<SvgEventSpec, Registration>? = null

    open fun setTargetAttribute(name: String, value: Any?) {
        SvgUtils.setAttribute(target, name, value)
    }

    open fun applyStyle() {}

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        conf.add(object : Synchronizer {
            private var myReg: Registration? = null

            override fun attach(ctx: SynchronizerContext) {
                applyStyle()

                myReg = source.addListener(object : SvgElementListener {
                    override fun onAttrSet(event: SvgAttributeEvent<*>) {
                        setTargetAttribute(event.attrSpec.name, event.newValue)
                    }
                })

                for (key in source.attributeKeys) {
                    val name = key.name
                    val value = source.getAttribute(name).get()
                    setTargetAttribute(name, value)
                }
            }

            override fun detach() {
                myReg!!.remove()
            }
        })
    }
}
