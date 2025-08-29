/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.compose.desktop

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import java.awt.Cursor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SvgViewPanel(
    svgView: SvgView,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
    var clickCount by remember { mutableStateOf(0) }
    var lastClickTime by remember { mutableStateOf(0L) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    var redrawTrigger by remember { mutableStateOf(0) }

    // Set up the redraw callback
    DisposableEffect(svgView) {
        svgView.onRedrawRequested = {
            redrawTrigger++
        }
        onDispose {
            svgView.onRedrawRequested = null
        }
    }

    // Force recomposition when SVG content or canvas size changes
    val svg = svgView.svg
    LaunchedEffect(svg, canvasSize) {
        if (canvasSize.width > 0 && canvasSize.height > 0) {
            redrawTrigger++
        }
    }

    Canvas(
        modifier = modifier
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.CROSSHAIR_CURSOR)))
            .onSizeChanged { size ->
                val width = (size.width / density).toInt()
                val height = (size.height / density).toInt()
                canvasSize = IntSize(width, height)
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Press -> {
                                val currentTime = System.currentTimeMillis()
                                clickCount = if (currentTime - lastClickTime < 300) {
                                    clickCount + 1
                                } else {
                                    1
                                }
                                lastClickTime = currentTime

                                svgView.handlePointerEvent(event)
                            }

                            PointerEventType.Release -> {
                                if (clickCount > 0) {
                                    val position = event.changes.first().position
                                    svgView.handleClick(position, clickCount)
                                    if (clickCount > 1) {
                                        clickCount = 0 // Reset after a double click
                                    }
                                }
                                svgView.handlePointerEvent(event)
                            }

                            PointerEventType.Move,
                            PointerEventType.Enter,
                            PointerEventType.Exit,
                            PointerEventType.Scroll -> {
                                svgView.handlePointerEvent(event)
                            }
                        }
                    }
                }
            }
    ) {
        drawSvgContent(svgView, this, canvasSize, redrawTrigger)
    }
}

private fun drawSvgContent(
    svgView: SvgView,
    drawScope: DrawScope,
    canvasSize: IntSize,
    redrawTrigger: Int // This parameter ensures the function is called when content changes
) {
    if (canvasSize.width <= 0 || canvasSize.height <= 0) return
    if (svgView.svg.width().get() == null || svgView.svg.height().get() == null) return

    svgView.render(drawScope)
}

