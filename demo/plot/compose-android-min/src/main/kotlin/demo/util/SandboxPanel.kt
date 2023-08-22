/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView

@Suppress("FunctionName")
@Composable
fun SandboxPanel(
    color: Color,
    modifier: Modifier,
) {
    val provider = SandboxPanelProvider(color)

    DisposableEffect(provider) {
        onDispose {
            provider.onDispose()
        }
    }

    val factory: (Context) -> View = provider.factory

    AndroidView(
        modifier = modifier,
        factory = factory,
    )
}

private class SandboxPanelProvider(
    private val color: Color
) {
    private var providedView: ViewGroup? = null

    val factory: (Context) -> View = { ctx ->
        check(providedView == null) { "Attempt to reuse a single-use view factory." }
        SandboxView(ctx, color).also {
            providedView = it
        }
    }

    fun onDispose() {
        println("onDispose")
        providedView?.removeAllViews()   // ToDo: dispose
        providedView = null
    }
}

private class SandboxView(
    private val context: Context,
    color: Color
) : RelativeLayout(context) {
    init {
        background = object : Drawable() {
            override fun draw(canvas: Canvas) {
                canvas.drawRect(
                    canvas.clipBounds,
                    Paint().also { it.color = color.toArgb() }
                )
            }

            override fun setAlpha(alpha: Int) {
            }

            override fun setColorFilter(colorFilter: ColorFilter?) {
            }

            override fun getOpacity(): Int {
                return PixelFormat.OPAQUE
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        println("onSizeChanged w: $w h: $h, oldw: $oldw, oldh: $oldh")
        super.onSizeChanged(w, h, oldw, oldh)

        // https://stackoverflow.com/questions/25516363/how-to-properly-add-child-views-to-view-group
        post {
            removeAllViews() // ToDo: dispose

            val childView = object : View(context) {
                override fun onDraw(canvas: Canvas?) {
                    super.onDraw(canvas)
                    canvas?.let { c ->
                        val r = c.clipBounds
                        c.drawRect(r, Paint().also { it.color = Color.Yellow.toArgb() })
                    }
                }

                override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
                    println("child onSizeChanged w: $w h: $h, oldw: $oldw, oldh: $oldh")
                    super.onSizeChanged(w, h, oldw, oldh)
                }
            }

            val params = RelativeLayout.LayoutParams(w / 2, h / 2).also {
                it.leftMargin = w / 4
                it.topMargin = h / 4
            }
            addView(childView, params)
        }
    }
}