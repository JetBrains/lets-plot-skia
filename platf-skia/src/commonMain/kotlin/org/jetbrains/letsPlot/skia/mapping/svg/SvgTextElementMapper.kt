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
        val sourceTextRunProperty = sourceTextRunProperty(source.children())
        val targetTextRunProperty = targetTextRunProperty(target)
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

    companion object {
        private fun sourceTextRunProperty(nodes: ObservableCollection<SvgNode>): ReadableProperty<List<Text.TextRun>> {
            fun textRuns(nodes: ObservableCollection<SvgNode>): List<Text.TextRun> {
                return nodes.flatMap { node ->
                    val nodeTextRuns = when (node) {
                        is SvgTextNode -> listOf(Text.TextRun(node.textContent().get()))
                        is SvgTSpanElement -> node.children().map { child ->
                            require(child is SvgTextNode)
                            val fontScale = node.getAttribute("font-size").get()?.let {
                                require(it is String) { "font-size: only string value is supported" }
                                when {
                                    "em" in it -> it.removeSuffix("em").toFloat()
                                    "%" in it -> it.removeSuffix("%").toFloat() / 100.0f
                                    else -> null
                                }
                            }
                            // TODO: replace with Specs from LP
                            val baselineShift = node.getAttribute("baseline-shift").get()?.let {
                                when (it) {
                                    "sub" -> Text.BaselineShift.SUB
                                    "super" -> Text.BaselineShift.SUPER
                                    else -> error("Unexpected baseline-shift value: $it")
                                }
                            }

                            val dy = node.getAttribute("dy").get()?.let {
                                require(it is String) { "dy: only string value is supported" }
                                when {
                                    "em" in it -> it.removeSuffix("em").toFloat()
                                    "%" in it -> it.removeSuffix("%").toFloat() / 100.0f
                                    else -> null
                                }
                            }

                            Text.TextRun(
                                text = child.textContent().get(),
                                baselineShift = baselineShift ?: Text.BaselineShift.NONE,
                                dy = dy ?: 0f,
                                fontScale = fontScale ?: 1f,
                            )
                        }

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

        private fun targetTextRunProperty(target: Text): WritableProperty<List<Text.TextRun>?> {
            return object : WritableProperty<List<Text.TextRun>?> {
                override fun set(value: List<Text.TextRun>?) {
                    target.content = value ?: emptyList()
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
}
