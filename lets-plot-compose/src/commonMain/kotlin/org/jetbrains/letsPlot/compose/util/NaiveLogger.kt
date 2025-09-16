/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.compose.util

private const val ENABLED = false

class NaiveLogger(val key: String) {
    fun print(s: String) {
        if (ENABLED) {
            println("[$key] $s")
        }
    }

    fun print(message: () -> String) {
        if (ENABLED) {
            print(message())
        }
    }
}