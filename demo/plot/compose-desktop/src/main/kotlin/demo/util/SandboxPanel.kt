/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.NoOpUpdate
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import java.awt.Color
import java.awt.Component
import java.awt.Rectangle
import javax.swing.JPanel

@Suppress("FunctionName")
@Composable
fun SandboxPanel(
    color: Color,
    modifier: Modifier,

    ) {
    val provider = SandboxPanelProvider(color)

    val factory: () -> Component = provider.factory

    DisposableEffect(factory) {
        onDispose {
            println("onDispose $this")
        }
    }

    val density = LocalDensity.current.density
    val modifier1 = modifier.onGloballyPositioned { coordinates ->
        val size = coordinates.size
        val width = size.width / density
        val height = size.height / density

        println("onGloballyPositioned $size density: $density")
        provider.onGloballyPositioned(width / 2, height / 2)
    }
    SwingPanel(
        background = androidx.compose.ui.graphics.Color.White,
        factory = factory,
        modifier = modifier1,
        update = NoOpUpdate
    )
}

private class SandboxPanelProvider(color: Color) {
    private val container = JPanel().apply {
        isOpaque = true
        background = color
        layout = null
    }

    val factory: () -> Component = { container }

    fun onGloballyPositioned(w: Float, h: Float) {
        container.removeAll() // ToDo: dispose
        container.add(
            JPanel().also {
                it.bounds = Rectangle(0, 0, w.toInt(), h.toInt())
                it.isOpaque = true
                it.background = Color.YELLOW
            }
        )
    }
}