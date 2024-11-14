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
import org.jetbrains.letsPlot.skia.mapping.svg.attr.SvgTSpanElementAttrMapping
import org.jetbrains.letsPlot.skia.mapping.svg.attr.SvgTextElementAttrMapping
import org.jetbrains.letsPlot.skia.shape.Element
import org.jetbrains.letsPlot.skia.shape.TSpan
import org.jetbrains.letsPlot.skia.shape.TextBlock
import org.jetbrains.letsPlot.skia.shape.asSkiaColor
import org.jetbrains.skia.FontStyle

internal class SvgTextElementMapper(
    source: SvgTextElement,
    target: TextBlock,
    peer: SvgSkiaPeer
) : SvgElementMapper<SvgTextElement, TextBlock>(source, target, peer) {

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
        val sourceTextRunProperty = sourceTextRunProperty(source.children(), peer.styleSheet, peer.fontManager)
        val targetTextRunProperty = targetTextRunProperty(target, peer.fontManager)
        conf.add(
            Synchronizers.forPropsOneWay(
                sourceTextRunProperty,
                targetTextRunProperty
            )
        )
    }

    private fun setFontProperties(target: TextBlock, styleSheet: StyleSheet?) {
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
                else -> error("Unknown fontStyle: `${style.face}`")
            }

            myTextAttrSupport.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, "fill:${style.color.toHexColor()};")
        }
    }

    private fun setFontProperties(target: TSpan, styleSheet: StyleSheet?) {
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
                else -> error("Unknown fontStyle: `${style.face}`")
            }

            myTextAttrSupport.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, "fill:${style.color.toHexColor()};")
        }
    }


    private fun targetTextRunProperty(textBlock: TextBlock, fontManager: FontManager): WritableProperty<List<Element>?> {
        return object : WritableProperty<List<Element>?> {
            override fun set(value: List<Element>?) {
                textBlock.children.clear()
                value?.forEach { textBlock.children.add(it) }
                textBlock.layoutChildren()
            }
        }
    }


    companion object {
        private fun sourceTextRunProperty(
            nodes: ObservableCollection<SvgNode>,
            styleSheet: StyleSheet?,
            fontManager: FontManager
        ): ReadableProperty<List<TSpan>> {
            fun toTSpans(nodes: ObservableCollection<SvgNode>): List<TSpan> {
                return nodes.flatMap { node ->
                    val nodeTextRuns = when (node) {
                        is SvgTextNode -> listOf(TSpan(fontManager).apply { text = node.textContent().get() })
                        is SvgTSpanElement -> handleTSpanElement(node, styleSheet, fontManager)
                        is SvgAElement -> handleAElement(node, styleSheet, fontManager)

                        else -> error("Unexpected node type: ${node::class.simpleName}")
                    }

                    nodeTextRuns
                }
            }

            return object : SimpleCollectionProperty<SvgNode, List<TSpan>>(nodes, toTSpans(nodes)) {
                override val propExpr = "textRuns($collection)"
                override fun doGet() = toTSpans(collection)
            }
        }


        private fun handleTSpanElement(node: SvgTSpanElement, styleSheet: StyleSheet?, fontManager: FontManager): List<TSpan> =
            node.children().map { child ->
                require(child is SvgTextNode)
                val style = styleSheet?.getTextStyle(node.fullClass())

                val tspan = TSpan(fontManager).apply {
                    text = child.textContent().get()
                    if (style?.isNoneColor == false) {
                        fill = style.color.asSkiaColor
                    }

                    SvgTSpanElementAttrMapping.setAttributes(this, node)
                }

                if (styleSheet == null) {
                    return@map tspan
                }
                val className = node.fullClass()
                if (className.isNotEmpty()) {
                    val style = styleSheet.getTextStyle(className)
                    tspan.fill = style.color.asSkiaColor
                    tspan.fontFamily = style.family.split(",").map { it.trim(' ', '"') }
                    tspan.fontSize = style.size.toFloat()
                    tspan.fontStyle = when {
                        style.face.bold && !style.face.italic -> FontStyle.BOLD
                        style.face.bold && style.face.italic -> FontStyle.BOLD_ITALIC
                        !style.face.bold && style.face.italic -> FontStyle.ITALIC
                        !style.face.bold && !style.face.italic -> FontStyle.NORMAL
                        else -> error("Unknown fontStyle: `${style.face}`")
                    }

                }

                return@map tspan
            }

        private fun handleAElement(node: SvgAElement, styleSheet: StyleSheet?, fontManager: FontManager): List<TSpan> {
            val href = node.getAttribute("href").get() as String
            return node.children().flatMap { child ->
                require(child is SvgTSpanElement)
                handleTSpanElement(child, styleSheet, fontManager)
            }
        }

        private class TextAttributesSupport(val target: TextBlock) {
            private var mySvgTextAnchor: String? = null

            fun setAttribute(name: String, value: Any?) {
                if (name == SvgTextContent.TEXT_ANCHOR.name) {
                    mySvgTextAnchor = value as String?
                }
                SvgTextElementAttrMapping.setAttribute(target, name, value)
            }
        }
    }
}