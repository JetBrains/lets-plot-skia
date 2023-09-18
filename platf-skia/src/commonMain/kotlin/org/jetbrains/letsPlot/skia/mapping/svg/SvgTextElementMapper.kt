/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.mapping.svg

import org.jetbrains.letsPlot.commons.intern.observable.collections.ObservableCollection
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.SimpleCollectionProperty
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizers
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet
import org.jetbrains.letsPlot.skia.mapping.svg.attr.SvgTextElementAttrMapping
import org.jetbrains.letsPlot.skia.shape.Text
import org.jetbrains.letsPlot.skia.shape.asSkiaColor
import org.jetbrains.skia.FontStyle

internal class SvgTextElementMapper(
    source: SvgTextElement,
    target: Text,
    peer: SvgSkiaPeer
) : SvgElementMapper<SvgTextElement, Text>(source, target, peer) {

    private val myTextAttrSupport = TextAttributesSupport(target)

    override fun setTargetAttribute(name: String, value: Any?) {
        myTextAttrSupport.setAttribute(name, value)
    }

    override fun applyStyle() {
        setFontProperties(target, peer.styleSheet)
    }

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        // Sync TextNodes, TextSpans
        val sourceTextProperty = sourceTextProperty(source.children())
        conf.add(
            Synchronizers.forPropsOneWay(
                sourceTextProperty,
                targetTextProperty(target)
            )
        )
    }

    private fun setFontProperties(target: Text, styleSheet: StyleSheet?) {
        if (styleSheet == null) {
            return
        }
        val className = source.fullClass()
        if (className.isNotEmpty()) {
            val style = styleSheet.getTextStyle(className)
            target.fill = style.color.asSkiaColor
            target.fontFamily = style.family.split(",").map { it.trim(' ', '"') }
            target.fontSize = style.size.toFloat()
            target.fontStyle = when {
                style.face.bold && !style.face.italic -> FontStyle.BOLD
                style.face.bold && style.face.italic -> FontStyle.BOLD_ITALIC
                !style.face.bold && style.face.italic -> FontStyle.ITALIC
                !style.face.bold && !style.face.italic -> FontStyle.NORMAL
                else -> error("Unknown fontStyle: `${style.face.toString()}`")
            }

            myTextAttrSupport.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, "fill:${style.color.toHexColor()};")
        }
    }

    companion object {
        private fun sourceTextProperty(nodes: ObservableCollection<SvgNode>): ReadableProperty<String> {
            return object : SimpleCollectionProperty<SvgNode, String>(nodes, joinToString(nodes)) {
                override val propExpr = "joinToString($collection)"
                override fun doGet() = joinToString(collection)
            }
        }

        private fun joinToString(nodes: ObservableCollection<SvgNode>): String {
            return nodes.asSequence()
                .flatMap { ((it as? SvgTSpanElement)?.children() ?: listOf(it as SvgTextNode)).asSequence() }
                .joinToString("\n") { (it as SvgTextNode).textContent().get() }
        }

        private fun targetTextProperty(target: Text): WritableProperty<String?> {
            return object : WritableProperty<String?> {
                override fun set(value: String?) {
                    target.text = value ?: "n/a"
                }
            }
        }
    }

    private class TextAttributesSupport(val target: Text) {
        private var mySvgTextAnchor: String? = null

        fun setAttribute(name: String, value: Any?) {
            if (name == SvgTextContent.TEXT_ANCHOR.name) {
                mySvgTextAnchor = value as String?
            }
            SvgTextElementAttrMapping.setAttribute(target, name, value)
        }
    }
}