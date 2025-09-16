/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.compose.desktop

import javax.swing.Timer

sealed interface DebouncedRunner {
    fun run()
    fun cancel()

    companion object {
        fun debounce(delayMs: Int, action: () -> Unit): DebouncedRunner = when (delayMs) {
            0 -> InstantRunner(action)
            else -> Debouncer(delayMs, action)
        }
    }

    private class Debouncer(
        delayMs: Int,
        action: () -> Unit,
    ) : DebouncedRunner {
        private val timer = Timer(delayMs) { action() }.apply { isRepeats = false }
        override fun run() = when (timer.isRunning) {
            true -> timer.restart()
            false -> timer.start()
        }

        override fun cancel() {
            if (timer.isRunning) {
                timer.stop()
            }
        }
    }

    private class InstantRunner(private val action: () -> Unit) : DebouncedRunner {
        override fun run() = action()
        override fun cancel() = Unit
    }
}