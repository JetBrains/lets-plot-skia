/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.svg.mapper

import org.jetbrains.letsPlot.commons.encoding.Base64
import org.jetbrains.letsPlot.commons.encoding.RGBEncoder
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.mapping.framework.MapperFactory
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.skia.shape.*
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.ImageInfo

internal class SvgNodeMapperFactory(private val peer: SvgSkiaPeer) : MapperFactory<SvgNode, Element> {
    companion object {
        private val LOG = PortableLogging.logger(SvgNodeMapperFactory::class)
    }

    override fun createMapper(source: SvgNode): Mapper<out SvgNode, out Element> {
        var src = source
        val target = SvgUtils.newElement(src)

        if (src is SvgImageElementEx) {
            src = src.asImageElement(SkiaRGBEncoder)
        }

        return when (src) {
            is SvgStyleElement -> SvgStyleElementMapper(
                src,
                target as Group,
                peer
            )

            is SvgGElement -> SvgGElementMapper(src, target as Group, peer)
            is SvgSvgElement -> SvgSvgElementMapper(src, peer)
            is SvgTextElement -> SvgTextElementMapper(src, target as Text, peer)
//            is SvgTextNode -> result = SvgTextNodeMapper(src, target as Text, myDoc, peer)
            is SvgImageElement -> SvgImageElementMapper(
                src,
                target as Image,
                peer
            )

            is SvgElement -> SvgElementMapper(src, target, peer)
            else -> throw IllegalArgumentException("Unsupported SvgElement: " + src::class.simpleName)
        }
    }

    object SkiaRGBEncoder : RGBEncoder {
        override fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String {
            val bytes = argbValues.flatMap {
                listOf(
                    (it shr 0) and 0xff,
                    (it shr 8) and 0xff,
                    (it shr 16) and 0xff,
                    (it shr 24) and 0xff,
                ).map(Int::toByte)
            }.toByteArray()

            val image = SkImage.makeRaster(ImageInfo.makeN32(width, height, ColorAlphaType.UNPREMUL), bytes, width * 4)
            val png = image.encodeToData(EncodedImageFormat.PNG)

            if (png == null) {
                LOG.error(IllegalStateException("Image encoding failed")) { "Image encoding failed" }
                return "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw=="
            }
            val encodedPng = Base64.encode(png.bytes)
            return "data:image/png;base64,$encodedPng"
        }
    }
}