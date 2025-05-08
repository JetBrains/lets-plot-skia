package org.jetbrains.letsPlot.skia.android.view

import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup
import org.jetbrains.letsPlot.android.canvas.AndroidCanvasControl
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.*
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElementListener
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.raster.view.CanvasEventDispatcher
import org.jetbrains.letsPlot.raster.view.SvgCanvasView

class SvgCanvasView(
    context: Context,
    svg: SvgSvgElement = SvgSvgElement(),
    eventDispatcher: CanvasEventDispatcher? = null
) : ViewGroup(context), Disposable, DisposingHub {
    var svg: SvgSvgElement
        get() = svgCanvasView.svg
        set(value) {
            svgCanvasView.svg = value
        }

    var eventDispatcher: CanvasEventDispatcher?
        get() = svgCanvasView.eventDispatcher
        set(value) {
            svgCanvasView.eventDispatcher = value
        }

    private var androidCanvasControl: AndroidCanvasControl? = null

    private val svgCanvasView = object : SvgCanvasView() {
        override fun createCanvasControl(view: SvgCanvasView): CanvasControl {
            if (androidCanvasControl != null) {
                return androidCanvasControl!!
            }

            val w = 600//width
            val h = 400//height

            val androidCanvasControl = AndroidCanvasControl(
                size = Vector(w, h),
                mouseEventSource = object : MouseEventSource {
                    override fun addEventHandler(eventSpec: MouseEventSpec, handler: EventHandler<MouseEvent>): Registration {
                        return eventDispatcher?.addEventHandler(eventSpec, handler)
                            ?: throw IllegalStateException("No event dispatcher")
                    }
                },
                context = context
            )

            this@SvgCanvasView.androidCanvasControl = androidCanvasControl

            return androidCanvasControl
        }

        override fun onHrefClick(href: String) {
            TODO("Not yet implemented")
        }

        override fun updateCanvasSize(width: Int, height: Int) {
            println("updateCanvasSize: $width x $height")
        }

    }
    private val registrations = CompositeRegistration()
    private val plotGestureDetector: PlotGestureDetector

    init {
        this.svg = svg
        this.eventDispatcher = eventDispatcher

        (svgCanvasView.canvasControl as AndroidCanvasControl).attachTo(this)
        //skikoView.skiaLayer.attachTo(this)

        registrations.add(
            svg.addListener(object : SvgElementListener {
                override fun onAttrSet(event: SvgAttributeEvent<*>) {
                    if (SvgConstants.HEIGHT.equals(event.attrSpec.name, ignoreCase = true) ||
                        SvgConstants.WIDTH.equals(event.attrSpec.name, ignoreCase = true)
                    ) {
                        throw IllegalStateException("Can't change SVG attribute $(event.attrSpec.name)")
                    }
                }
            })
        )
        plotGestureDetector = PlotGestureDetector(context, this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val density = resources.displayMetrics.density
        val width = (svgCanvasView.width * density).toInt()
        val height = (svgCanvasView.height * density).toInt()

        println("onMeasure: $width x $height, density: $density")

        measureChild(
            getChildAt(0),
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        getChildAt(0).apply {
            layout(0, 0, measuredWidth, measuredHeight)
        }
    }

    override fun registerDisposable(disposable: Disposable) {
        registrations.add(DisposableRegistration(disposable))
    }

    override fun dispose() {
        removeAllViews()

        // TODO: looks like a skiko bug
        // Need to remove skikoView from parent ViewGroup and dispose in post() to avoid crash:
        //  signal 11 (SIGSEGV), code 1 (SEGV_MAPERR), fault addr 0x4
        //  Cause: null pointer dereference
        //  backtrace:
        //        #00 pc 000000000000dcc8  /system/lib64/libutils.so (android::RefBase::incStrong(void const*) const+8) (BuildId: a3acb0eba7fd91ea48db6f0befa41c65)
        //        #01 pc 0000000000127ef8  /system/lib64/libandroid_runtime.so (android::nativeSetFlags(_JNIEnv*, _jclass*, long, long, int, int)+84) (BuildId: db04fb413e0fca096fdfcb015acb2940)
        //        #02 pc 0000000000187c88  /system/framework/arm64/boot-framework.oat (art_jni_trampoline+120) (BuildId: 56fb928acef1792118f6001991e3f57d774e28ed)
        //        ...
        //        #32 pc 000000000048ca0c  /system/lib64/libhwui.so (_JNIEnv::CallVoidMethod(_jobject*, _jmethodID*, ...)+120) (BuildId: a241a5dc738c2fe8686d91f83b94d911)
        //        #33 pc 0000000000491e58  /system/lib64/libhwui.so (_ZZN7androidL46android_view_RenderNode_requestPositionUpdatesEP7_JNIEnvP8_jobjectlS3_EN26PositionListenerTrampoline14onPositionLostERNS_10uirenderer10RenderNodeEPKNS5_8TreeInfoE$e08e5be7d6b9d462d36dec99da9de1de+192) (BuildId: a241a5dc738c2fe8686d91f83b94d911)
        //        #34 pc 00000000002c6cd8  /system/lib64/libhwui.so (android::uirenderer::RenderNode::deleteDisplayList(android::uirenderer::TreeObserver&, android::uirenderer::TreeInfo*)+348) (BuildId: a241a5dc738c2fe8686d91f83b94d911)
        //        #35 pc 000000000041c650  /system/lib64/libhwui.so (android::uirenderer::RenderNode::destroyHardwareResources(android::uirenderer::TreeInfo*)+92) (BuildId: a241a5dc738c2fe8686d91f83b94d911)
        //        #36 pc 000000000034b10c  /system/lib64/libhwui.so (android::uirenderer::MarkAndSweepRemoved::~MarkAndSweepRemoved()+288) (BuildId: a241a5dc738c2fe8686d91f83b94d911)
        //        #37 pc 000000000034af68  /system/lib64/libhwui.so (android::uirenderer::RenderNode::prepareTree(android::uirenderer::TreeInfo&)+164) (BuildId: a241a5dc738c2fe8686d91f83b94d911)
        //        #38 pc 00000000004cf218  /system/lib64/libhwui.so (android::uirenderer::RootRenderNode::prepareTree(android::uirenderer::TreeInfo&)+176) (BuildId: a241a5dc738c2fe8686d91f83b94d911)
        //        #39 pc 00000000003ba42c  /system/lib64/libhwui.so (android::uirenderer::renderthread::CanvasContext::prepareTree(android::uirenderer::TreeInfo&, long*, long, android::uirenderer::RenderNode*)+336) (BuildId: a241a5dc738c2fe8686d91f83b94d911)
        //        #40 pc 00000000003b99fc  /system/lib64/libhwui.so (_ZNSt3__110__function6__funcIZN7android10uirenderer12renderthread13DrawFrameTask11postAndWaitEvE3$_0NS_9allocatorIS6_EEFvvEEclEv$c1671e787f244890c877724752face20+420) (BuildId: a241a5dc738c2fe8686d91f83b94d911)
        //        #41 pc 00000000003c68d8  /system/lib64/libhwui.so (android::uirenderer::WorkQueue::process()+156) (BuildId: a241a5dc738c2fe8686d91f83b94d911)
        //        #42 pc 00000000003c6644  /system/lib64/libhwui.so (android::uirenderer::renderthread::RenderThread::threadLoop()+84) (BuildId: a241a5dc738c2fe8686d91f83b94d911)
        //        ...

        // Didn't see any fixes in skiko, but now it works without post().
        //post {
        registrations.dispose()
        svgCanvasView.dispose()
        //}
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        plotGestureDetector.onTouchEvent(event)
        return true
    }

}