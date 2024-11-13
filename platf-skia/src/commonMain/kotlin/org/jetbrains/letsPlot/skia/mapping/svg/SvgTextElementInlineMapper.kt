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
import org.jetbrains.letsPlot.skia.shape.Text
import org.jetbrains.letsPlot.skia.shape.asSkiaColor
import org.jetbrains.skia.FontStyle

internal class SvgTextElementInlineMapper(
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
        val sourceTextRunProperty = sourceTextRunProperty(source.children(), peer.styleSheet)
        val targetTextRunProperty = targetTextRunProperty(target, peer.fontManager)
        conf.add(
            Synchronizers.forPropsOneWay(
                sourceTextRunProperty,
                targetTextRunProperty
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
                else -> error("Unknown fontStyle: `${style.face}`")
            }

            myTextAttrSupport.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, "fill:${style.color.toHexColor()};")
        }
    }


    private fun targetTextRunProperty(target: Text, fontManager: FontManager): WritableProperty<List<Text.TextRun>?> {
        return object : WritableProperty<List<Text.TextRun>?> {
            override fun set(value: List<Text.TextRun>?) {
                target.content = value ?: emptyList()
            }
        }
    }


    companion object {
        private fun sourceTextRunProperty(
            nodes: ObservableCollection<SvgNode>,
            styleSheet: StyleSheet?
        ): ReadableProperty<List<Text.TextRun>> {
            fun textRuns(nodes: ObservableCollection<SvgNode>): List<Text.TextRun> {
                return nodes.flatMap { node ->
                    val nodeTextRuns = when (node) {
                        is SvgTextNode -> listOf(Text.TextRun(node.textContent().get()))
                        is SvgTSpanElement -> handleTSpanElement(node, styleSheet)
                        is SvgAElement -> handleAElement(node, styleSheet)

                        else -> error("Unexpected node type: ${node::class.simpleName}")
                    }

                    nodeTextRuns
                }
            }

            return object : SimpleCollectionProperty<SvgNode, List<Text.TextRun>>(nodes, textRuns(nodes)) {
                override val propExpr = "textRuns($collection)"
                override fun doGet() = textRuns(collection)
            }
        }


        private fun handleTSpanElement(node: SvgTSpanElement, styleSheet: StyleSheet?): List<Text.TextRun> =
            node.children().map { child ->
                require(child is SvgTextNode)
                val textRun = Text.TextRun(child.textContent().get())
                SvgTSpanElementAttrMapping.setAttributes(textRun, node)

                val style = styleSheet?.getTextStyle(node.fullClass())

                if (style?.isNoneColor == false) {
                    textRun.fill = style.color.asSkiaColor
                }
                textRun
            }

        private fun handleAElement(node: SvgAElement, styleSheet: StyleSheet?): List<Text.TextRun> {
            val href = node.getAttribute("href").get() as String
            return node.children().flatMap { child ->
                require(child is SvgTSpanElement)
                handleTSpanElement(child, styleSheet)
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
}
