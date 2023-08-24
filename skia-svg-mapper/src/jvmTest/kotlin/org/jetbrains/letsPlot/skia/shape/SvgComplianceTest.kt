/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.skia.svg.mapper.SvgSkiaPeer
import org.jetbrains.letsPlot.skia.svg.mapper.SvgSvgElementMapper
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Rect
import kotlin.test.Test

class SvgComplianceTest {
    @Test
    fun `nested g - empty`() {
        val root = mapSvg {
            svgDocument(width = 400, height = 300) {
                g(translate(x = 10, y = 20))
            }
        }

        root.takeChild<Group>(0).let {
            assertThat(it.transform!!.mat).containsExactly(*Matrix33.makeTranslate(10f, 20f).mat)
            assertThat(it.localBounds).isEqualTo(Rect.makeXYWH(0f, 0f, 0f, 0f))
            assertThat(it.screenBounds).isEqualTo(Rect.makeXYWH(10f, 20f, 0f, 0f))
            assertThat(it.ctm.mat).containsExactly(*Matrix33.makeTranslate(10f, 20f).mat)
        }
    }


    @Test
    fun `nested g - simple`() {
        val root = mapSvg {
            svgDocument(width = 400, height = 300) {
                g(translate(x = 10, y = 20)) {
                    rect(5, 16, 40, 15, fill=SvgColors.RED)
                }
            }
        }

        root.takeChild<Rectangle>(0, 0).let {
            assertThat(it.screenBounds).isEqualTo(Rect.makeXYWH(15f, 36f, 40f, 15f))
        }

        root.takeChild<Group>(0).let {
            assertThat(it.transform!!.mat).containsExactly(*Matrix33.makeTranslate(10f, 20f).mat)
            assertThat(it.localBounds).isEqualTo(Rect.makeXYWH(5f, 16f, 40f, 15f))
            assertThat(it.screenBounds).isEqualTo(Rect.makeXYWH(15f, 36f, 40f, 15f))
            assertThat(it.ctm.mat).containsExactly(*Matrix33.makeTranslate(10f, 20f).mat)
        }
    }

    @Test
    fun `nested svg - empty`() {
        val root = mapSvg {
            svgDocument(width = 400, height = 300) {
                svg(x = 10, y = 20, width = 30, height = 40)
            }
        }

        root.takeChild<Pane>(0).let {
            assertThat(it.transform).isNull()
            assertThat(it.localBounds).isEqualTo(Rect.makeXYWH(0f, 0f, 0f, 0f))
            assertThat(it.screenBounds).isEqualTo(Rect.makeXYWH(10f, 20f, 0f, 0f))
            assertThat(it.ctm.mat).containsExactly(*Matrix33.makeTranslate(10f, 20f).mat)
        }
    }

    @Test
    fun `nested svg - simple`() {
        val root = mapSvg {
            svgDocument(width = 400, height = 300) {
                svg(x = 10, y = 20, width = 400, height = 300) {
                    rect(5, 16, 40, 15, fill=SvgColors.RED)
                }
            }
        }

        root.takeChild<Rectangle>(0, 0).let {
            assertThat(it.ctm.mat).containsExactly(*Matrix33.makeTranslate(10f, 20f).mat)
            assertThat(it.screenBounds).isEqualTo(Rect.makeXYWH(15f, 36f, 40f, 15f))
        }

        root.takeChild<Pane>(0).let {
            assertThat(it.transform).isNull()
            assertThat(it.localBounds).isEqualTo(Rect.makeXYWH(5f, 16f, 40f, 15f))
            assertThat(it.screenBounds).isEqualTo(Rect.makeXYWH(15f, 36f, 40f, 15f))
            assertThat(it.ctm.mat).containsExactly(*Matrix33.makeTranslate(10f, 20f).mat)
        }
    }

    @Test
    fun `group - empty children should not affect measurement`() {
        val root = mapSvg {
            svgDocument(width = 400, height = 300) {
                g {
                    g(translate(50, 70)) {
                        rect(0, 0, 10, 20)
                        g(translate(500, 500)) // no children - should be excluded from measurement
                    }
                }
            }
        }

        root.takeChild<Group>(0).let {
            assertThat(it.localBounds).isEqualTo(Rect.makeXYWH(0f, 0f, 10f, 20f))
            assertThat(it.screenBounds).isEqualTo(Rect.makeXYWH(50f, 70f, 10f, 20f))
        }

        root.takeChild<Group>(0, 0).let {
            assertThat(it.localBounds).isEqualTo(Rect.makeXYWH(0f, 0f, 10f, 20f))
            assertThat(it.screenBounds).isEqualTo(Rect.makeXYWH(50f, 70f, 10f, 20f))
        }
    }

    @Test
    fun `rect xy doesnt affect screenTransform`() {
        val root = mapSvg {
            svgDocument(width = 400.0, height = 300.0) {
                g(translate(10.0, 20.0)) {
                    g(translate(30.0, 50.0)) {
                        rect(x=3f, y=5f, width = 10f, height = 10f)
                    }
                }
            }
        }

        root.takeChild<Rectangle>(0, 0, 0).let {
            assertThat(it.transform).isNull()
            assertThat(it.localBounds).isEqualTo(Rect.makeXYWH(3f, 5f, 10f, 10f))
            assertThat(it.screenBounds).isEqualTo(Rect.makeXYWH(43f, 75f, 10f, 10f))
            assertThat(it.ctm.mat).containsExactly(*Matrix33.makeTranslate(40f, 70f).mat)
        }
    }

    @Test
    fun nestedSvgSvgElement() {
        val root = mapSvg {
            svgDocument(width = 400.0, height = 300.0) {
                g(translate(10.0, 20.0)) {
                    svg(x = 13.0, y = 17.0, width = 180.0, height = 50.0) {
                        rect(x = 1f, y = 3f, width = 10f, height = 10f)
                    }
                }
            }
        }

        root.takeChild<Pane>(0, 0).let {
            assertThat(it.transform).isNull()
            assertThat(it.localBounds).isEqualTo(Rect.makeXYWH(1f, 3f, 10f, 10f))
            assertThat(it.ctm.mat).containsExactly(*Matrix33.makeTranslate(23f, 37f).mat)
            assertThat(it.screenBounds).isEqualTo(Rect.makeXYWH(24f, 40f, 10f, 10f))
        }


        root.takeChild<Rectangle>(0, 0, 0).let {
            assertThat(it.transform).isNull()
            assertThat(it.localBounds).isEqualTo(Rect.makeXYWH(1f, 3f, 10f, 10f))
            assertThat(it.screenBounds).isEqualTo(Rect.makeXYWH(24f, 40f, 10f, 10f))
            assertThat(it.ctm.mat).containsExactly(*Matrix33.makeTranslate(23f, 37f).mat)
        }
    }

    private fun mapSvg(builder: () -> SvgSvgElement): Pane {
        val svgDocument = builder()
        val node = SvgNodeContainer(svgDocument)
        val rootMapper = SvgSvgElementMapper(svgDocument, SvgSkiaPeer())
        rootMapper.attachRoot(MappingContext())
        return rootMapper.target
    }

    private fun svgDocument(
        x: Number? = null,
        y: Number? = null,
        width: Number? = null,
        height: Number? = null,
        config: SvgSvgElement.() -> Unit = {},
    ): SvgSvgElement {
        val el = SvgSvgElement()
        x?.let { el.x().set(it.toDouble()) }
        y?.let { el.y().set(it.toDouble()) }
        width?.let { el.width().set(it.toDouble()) }
        height?.let { el.height().set(it.toDouble()) }

        el.apply(config)
        return el
    }

    private fun SvgNode.svg(
        x: Number? = null,
        y: Number? = null,
        width: Number? = null,
        height: Number? = null,
        config: SvgSvgElement.() -> Unit = {},
    ): SvgSvgElement {
        val el = svgDocument(x, y, width, height, config)

        children().add(el)
        el.apply(config)
        return el
    }

    private fun SvgNode.g(
        transform: SvgTransform? = null,
        config: SvgGElement.() -> Unit = {},
    ): SvgGElement {
        val el = SvgGElement()
        transform?.let { el.transform().set(it) }
        el.apply(config)
        children().add(el)
        return el
    }

    fun SvgNode.path(
        stroke: SvgColor? = null,
        strokeOpacity: Number? = null,
        strokeWidth: Number? = null,
        fill: SvgColor? = null,
        fillOpacity: Number? = null,
        pathData: SvgPathData? = null,
        config: SvgPathElement.() -> Unit = {},
    ): SvgPathElement {
        val el = pathData?.let { SvgPathElement(it) } ?: SvgPathElement()
        stroke?.let { el.stroke().set(it) }
        strokeOpacity?.let { el.strokeOpacity().set(it.toDouble()) }
        strokeWidth?.let { el.strokeWidth().set(it.toDouble()) }
        fill?.let { el.fill().set(it) }
        fillOpacity?.let { el.fillOpacity().set(it.toDouble()) }

        el.apply(config)

        children().add(el)
        return el
    }

    private fun SvgNode.rect(
        x: Number? = null,
        y: Number? = null,
        width: Number? = null,
        height: Number? = null,
        stroke: SvgColor? = null,
        fill: SvgColor? = null,
        config: SvgRectElement.() -> Unit = {},
    ): SvgRectElement {
        val el = SvgRectElement()
        x?.let { el.x().set(it.toDouble()) }
        y?.let { el.y().set(it.toDouble()) }
        width?.let { el.width().set(it.toDouble()) }
        height?.let { el.height().set(it.toDouble()) }
        stroke?.let { el.stroke().set(it) }
        fill?.let { el.fill().set(it) }

        el.apply(config)
        children().add(el)
        return el
    }

    private fun SvgNode.text(
        text: String? = null,
        x: Number? = null,
        y: Number? = null,
        styleClass: String? = null,
        config: SvgTextElement.() -> Unit = {},
    ): SvgTextElement {
        val el = SvgTextElement()
        x?.let { el.x().set(it.toDouble()) }
        y?.let { el.y().set(it.toDouble()) }
        text?.let { el.setTextNode(it) }
        styleClass?.let { el.addClass(it) }

        el.apply(config)
        children().add(el)
        return el
    }

    private fun translate(x: Number, y: Number): SvgTransform {
        return SvgTransformBuilder().translate(x.toDouble(), y.toDouble()).build()
    }

    private inline fun <reified T> Parent.takeChild(vararg path: Int): T {
        var current: Parent = this
        path.dropLast(1).forEach { i ->
            current = current.children[i] as Parent
        }

        return current.children[path.last()] as T
    }
}