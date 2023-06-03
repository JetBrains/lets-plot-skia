package org.jetbrains.letsPlot.skiko

import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svg.SvgSvgElement
import org.jetbrains.letsPlot.skia.mapper.SvgSkiaPeer
import org.jetbrains.letsPlot.skia.mapper.SvgSvgElementMapper
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Drawable
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoView
import kotlin.math.ceil

abstract class SvgSkikoView constructor(
    svg: SvgSvgElement,
) : SkikoView, Disposable {

    private val nodeContainer = SvgNodeContainer(svg)  // attach root
    private val drawable: Drawable
    private lateinit var _nativeLayer: SkiaLayer

    val skiaLayer: SkiaLayer
        get() {
            if (!this::_nativeLayer.isInitialized) {
                _nativeLayer = createSkiaLayer(this)
            }
            return _nativeLayer
        }

    val width: Int = svg.width().get()?.let { ceil(it).toInt() } ?: 0
    val height: Int = svg.height().get()?.let { ceil(it).toInt() } ?: 0

    init {
        val rootMapper = SvgSvgElementMapper(svg, SvgSkiaPeer())
        rootMapper.attachRoot(MappingContext())
        drawable = rootMapper.target.drawable
    }

    protected abstract fun createSkiaLayer(view: SvgSkikoView): SkiaLayer

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        // ToDo: width, height ?
        canvas.scale(skiaLayer.contentScale, skiaLayer.contentScale)
        canvas.drawDrawable(drawable)
    }

    override fun dispose() {
//        AwtContainerDisposer(this).dispose()

        // Detach svg root.
        nodeContainer.root().set(SvgSvgElement())

        if (this::_nativeLayer.isInitialized) {
            _nativeLayer.dispose()
        }
    }
}