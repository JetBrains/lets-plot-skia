package org.jetbrains.letsPlot.skia.compose.util

private const val ENABLED = false

class NaiveLogger(val key: String) {
    fun print(s: String) {
        if (ENABLED) {
            println("[$key] $s")
        }
    }
}