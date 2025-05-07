/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.android.canvas


import org.jetbrains.letsPlot.commons.registration.Disposable
import java.util.Timer
import java.util.TimerTask


class AndroidAnimationTimerPeer(
    val executor: (() -> Unit) -> Unit,
    private val updateRate: Int = 60
) : Disposable {
    private val myHandlers = ArrayList<(Long) -> Unit>()
    private var isRunning = false

    private val actionListener = object : TimerTask() {
        override fun run() {
            myHandlers.forEach {
                executor {
                    it(System.currentTimeMillis())
                }
            }
        }
    }

    private val myTimer: Timer = Timer()

    fun addHandler(handler: (Long) -> Unit) {
        synchronized(myHandlers) {
            myHandlers.add(handler)

            if (!isRunning) {
                myTimer.schedule(actionListener, 0L, 1000L / updateRate)
            }
        }
    }

    fun removeHandler(handler: (Long) -> Unit) {
        synchronized(myHandlers) {
            myHandlers.remove(handler)

            if (myHandlers.isEmpty() && isRunning) {
                myTimer.cancel()
                isRunning = false
            }
        }
    }

    override fun dispose() {
        isRunning = false
        myTimer.cancel()
        myHandlers.clear()
    }
}