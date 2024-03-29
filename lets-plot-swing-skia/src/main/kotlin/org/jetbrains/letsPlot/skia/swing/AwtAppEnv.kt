/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.swing

import org.jetbrains.letsPlot.awt.plot.component.ApplicationContext
import javax.swing.SwingUtilities

internal object AwtAppEnv {
    val AWT_EDT_EXECUTOR = { action: () -> Unit ->
        runInEdt(
            Runnable {
                action()
            },
            canRunImmediately = true
        )
    }

    val AWT_APP_CONTEXT = object : ApplicationContext {
        override fun runWriteAction(action: Runnable) {
            action.run()
        }

        override fun invokeLater(action: Runnable, expared: () -> Boolean) {
            runInEdt(
                Runnable {
                    if (!expared()) {
                        action.run()
                    }
                },
                canRunImmediately = false
            )
        }
    }

    private fun runInEdt(action: Runnable, canRunImmediately: Boolean) {
        if (canRunImmediately && SwingUtilities.isEventDispatchThread()) {
            action.run()
        } else {
            SwingUtilities.invokeLater(action)
        }
    }
}